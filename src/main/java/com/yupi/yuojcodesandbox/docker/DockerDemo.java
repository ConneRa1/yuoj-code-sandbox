/**
 * copyright (C), 2015-2024
 * fileName: DockerDemo
 *
 * @author: mlt
 * date:    2024/12/5 上午10:17
 * description:
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/5 上午10:17           V1.0
 */
package com.yupi.yuojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

import java.util.List;

/**
 *
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/5
 */
public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        // 获得默认配置的dockerclient
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        String image="nginx:latest";

        // 拉取镜像
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println(item.toString());
                super.onNext(item);
            }
        };
        pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
        System.out.println("pullImageCmd.exec end");

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse containerResponse = containerCmd.withCmd("echo", "Hello Docker").exec();
        String id = containerResponse.getId();

        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containers = listContainersCmd.withShowAll(true).exec();
        for (Container container : containers) {
            System.out.println(container.getId());
        }

        // 执行容器
        dockerClient.startContainerCmd(id).exec();
        // 查看日志
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println(item.getStreamType());
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };

        // 阻塞等待日志输出
        dockerClient.logContainerCmd(id)
                .withStdErr(true)
                .withStdOut(true)
                .exec(logContainerResultCallback)
                .awaitCompletion();

    }
}
