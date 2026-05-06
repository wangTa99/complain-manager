package com.wt.complaint.manage.config;

import com.xiaomi.mone.current.threadpool.MoneThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhangzheyang
 * @date 2024/12/20
 */
@Configuration
public class ExecutorConfig {

    public static final String COMPLAINT_MANAGE = "complaint-manage";

    @Bean(name = "complaintTabCountExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor complaintTabCountExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "complaintTabCountExecutor");
    }

    @Bean(name = "commonThreadPoolExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor commonThreadPoolExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "commonThreadPoolExecutor");
    }

    @Bean(name = "createChatGroupExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor createChatGroupExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "createChatGroupExecutor");
    }

    @Bean(name = "complaintOrderListExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor complaintOrderListExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "complaintOrderListExecutor");
    }

    @Bean(name = "messageSendChangeExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor messageSendChangeExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "messageSendChangeExecutor");
    }

    @Bean(name = "constructMessageEventExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor constructMessageEventExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "constructMessageEventExecutor");
    }

    @Bean(name = "exportComplaintOrderListExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor exportComplaintOrderListExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "exportComplaintOrderListExecutor");
    }

    @Bean(name = "retailComplaintOrderListExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor retailComplaintOrderListExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "retailComplaintOrderListExecutor");
    }

    @Bean(name = "userComplaintOrderListExecutor", destroyMethod = "destroy")
    public MoneThreadPoolExecutor userComplaintOrderListExecutor() {
        return new MoneThreadPoolExecutor(COMPLAINT_MANAGE, "userComplaintOrderListExecutor");
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor submitFinishApplyExecutor() {
        return createThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, 50, 5, 1024,
                "submitFinishApplyExecutor-", new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor submitChangeApplyExecutor() {
        return createThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, 50, 5, 1024,
            "submitChangeApplyExecutor-", new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolTaskExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveSecods,
                                                           int queueCapacity, String threadNamePrefix, RejectedExecutionHandler handler) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveSecods);
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(handler);
        // 异步MDC
        executor.setTaskDecorator(new MdcDecorator());
        executor.initialize();

        return executor;
    }
}
