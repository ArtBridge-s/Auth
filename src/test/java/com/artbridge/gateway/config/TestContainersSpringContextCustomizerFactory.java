package com.artbridge.gateway.config;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.testcontainers.containers.KafkaContainer;
import tech.jhipster.config.JHipsterConstants;

public class TestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private Logger log = LoggerFactory.getLogger(TestContainersSpringContextCustomizerFactory.class);

    private static KafkaTestContainer kafkaBean;
    private static SqlTestContainer prodTestContainer;

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        return (context, mergedConfig) -> {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            TestPropertyValues testValues = TestPropertyValues.empty();
            EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedSQL.class);
            if (null != sqlAnnotation) {
                log.debug("detected the EmbeddedSQL annotation on class {}", testClass.getName());
                log.info("Warming up the sql database");
                if (
                    Arrays
                        .asList(context.getEnvironment().getActiveProfiles())
                        .contains("test" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
                ) {
                    if (null == prodTestContainer) {
                        try {
                            Class<? extends SqlTestContainer> containerClass = (Class<? extends SqlTestContainer>) Class.forName(
                                this.getClass().getPackageName() + ".MariadbTestContainer"
                            );
                            prodTestContainer = beanFactory.createBean(containerClass);
                            beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
                            // ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    testValues =
                        testValues.and(
                            "spring.r2dbc.url=" +
                            prodTestContainer.getTestContainer().getJdbcUrl().replace("jdbc", "r2dbc") +
                            "?useLegacyDatetimeCode=false&serverTimezone=UTC"
                        );
                    testValues = testValues.and("spring.r2dbc.username=" + prodTestContainer.getTestContainer().getUsername());
                    testValues = testValues.and("spring.r2dbc.password=" + prodTestContainer.getTestContainer().getPassword());
                    testValues =
                        testValues.and(
                            "spring.liquibase.url=" +
                            prodTestContainer.getTestContainer().getJdbcUrl() +
                            "?useLegacyDatetimeCode=false&serverTimezone=UTC"
                        );
                }
            }
            EmbeddedKafka kafkaAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedKafka.class);
            if (null != kafkaAnnotation) {
                log.debug("detected the EmbeddedKafka annotation on class {}", testClass.getName());
                log.info("Warming up the kafka broker");
                if (null == kafkaBean) {
                    kafkaBean = beanFactory.createBean(KafkaTestContainer.class);
                    beanFactory.registerSingleton(KafkaTestContainer.class.getName(), kafkaBean);
                    // ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(KafkaTestContainer.class.getName(), kafkaBean);
                }
                testValues =
                    testValues.and(
                        "spring.cloud.stream.kafka.binder.brokers=" +
                        kafkaBean.getKafkaContainer().getHost() +
                        ':' +
                        kafkaBean.getKafkaContainer().getMappedPort(KafkaContainer.KAFKA_PORT)
                    );
            }
            testValues.applyTo(context);
        };
    }
}
