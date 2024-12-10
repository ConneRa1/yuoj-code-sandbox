/**
 * copyright (C), 2015-2024
 * fileName: JavaNativeCodeSandbox
 *
 * @author: mlt
 * date:    2024/12/2 下午6:32
 * description:
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/2 下午6:32           V1.0
 */
package com.yupi.yuojcodesandbox;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.yupi.yuojcodesandbox.model.ExcuteReponse;
import com.yupi.yuojcodesandbox.model.ExcuteRequest;
import com.yupi.yuojcodesandbox.model.ExecuteMessage;
import com.yupi.yuojcodesandbox.model.JudgeInfo;
import com.yupi.yuojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.TIMEOUT;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/2
 */
@Slf4j
@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate{
    private static Boolean FIRST_PULL= false;
    private static final Integer RUN_TIME_OUT = 5000;
    private static final DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    private static final String image="openjdk:8-alpine";

    static {
        // 拉取镜像
        if(FIRST_PULL)
        {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println(item.toString());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像失败");
                throw new RuntimeException(e);
            }
            System.out.println("拉取镜像成功");
            FIRST_PULL=false;
        }
    }



/*    public static void main(String[] args) {
        JavaDockerCodeSandbox javaNativeCodeSandbox=new JavaDockerCodeSandbox();
        ExcuteRequest excuteRequest = new ExcuteRequest();
        excuteRequest.setInputList(Arrays.asList("1 2","2 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        //String code = ResourceUtil.readStr("testCode/ErrorCode/RunProcessError.java", StandardCharsets.UTF_8);
        excuteRequest.setCode(code);
        excuteRequest.setLanguage("java");
        ExcuteReponse excuteReponse = javaNativeCodeSandbox.excuteCode(excuteRequest);
        log.info("result:"+excuteReponse.toString());
    }*/

    @Override
    protected List<ExecuteMessage> getOutputList(List<String> inputList, File userCodeFile) {
        String userCodeParentRoot=userCodeFile.getParentFile().getAbsolutePath();
        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        // 在hostConfig里设置映射目录以及内存占用等参数
        hostConfig.setBinds(new Bind(userCodeParentRoot,new Volume("/app")));
        hostConfig.withMemory(100*1024*1024L);
        hostConfig.withCpuCount(1L);
        hostConfig.withMemorySwap(0L);
        CreateContainerResponse containerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withTty(true)
                // 限制网络资源
                .withNetworkDisabled(true)
                .exec();
        String id = containerResponse.getId();

        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containers = listContainersCmd.withShowAll(true).exec();
        for (Container container : containers) {
            System.out.println(container.getId());
        }

        // 执行容器
        dockerClient.startContainerCmd(id).exec();
        // 将命令和参数传入到容器中，并返回结果
        List<ExecuteMessage> messages=new ArrayList<>();
        final long[] maxMemory = {0L};
        final boolean[] timeout = {true};
        for(String inputArgs:inputList)
        {
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(id)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();

            String execId = execCreateCmdResponse.getId();
            ExecuteMessage executeMessage=new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};

            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    timeout[0] =false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (streamType == StreamType.STDOUT) {
                        message[0] =new String(frame.getPayload());
                        System.out.println(Arrays.toString(frame.getPayload()));
                    } else if (streamType == StreamType.STDERR) {
                        errorMessage[0] =new String(frame.getPayload());
                        System.out.println(Arrays.toString(frame.getPayload()));
                    }
                    super.onNext(frame);
                }
            };

            // 获取占用的内存
            StatsCmd statsCmd = dockerClient.statsCmd(id);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                @Override
                public void onNext(Statistics statistics) {
                    //System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void close() throws IOException {

                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });

            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        //限制超时时间
                        .awaitCompletion(RUN_TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                statsCmd.exec(statisticsResultCallback);
                statsCmd.close();
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessage.setMessage(message[0]);
                executeMessage.setErrorMessage(errorMessage[0]);
                messages.add(executeMessage);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }



}
