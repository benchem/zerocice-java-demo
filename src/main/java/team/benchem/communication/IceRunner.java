package team.benchem.communication;

import com.zeroc.Ice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.Exception;

/**
 * @ClassName IceRunner
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-16 12:14
 * @Version 1.0
 **/
@Component
public class IceRunner implements ApplicationRunner, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(IceRunner.class);
    static Communicator communicator;

    @Autowired
    JsonServiceCenter serviceCenter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("ice init");

        Properties props = Util.createProperties();
        props.setProperty("Ice.ThreadPool.Server.Size", "256");
        props.setProperty("Ice.ThreadPool.Server.SizeMax", "1000");
        props.setProperty("Ice.ThreadPool.Server.SizeWarn", "900");
        props.setProperty("Ice.ThreadPool.Client.Size", "20");
        props.setProperty("Ice.ThreadPool.Client.SizeMax", "100");
        props.setProperty("Ice.ThreadPool.Client.SizeWarn", "80");
        props.setProperty("Ice.MessageSizeMax", "5242880");

        InitializationData initData = new InitializationData();
        initData.properties = props;
        communicator = Util.initialize(initData);
        Runtime.getRuntime().addShutdownHook(new IceShutdownHook(communicator));
        ObjectAdapter adapter = communicator
                .createObjectAdapterWithEndpoints("LonntecRPC", "default -h 0.0.0.0 -p 9090");
        adapter.add(serviceCenter, Util.stringToIdentity("serviceCenter"));
        adapter.activate();

        logger.info("ice running");
        communicator.waitForShutdown();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    static class IceShutdownHook extends Thread{

        private final Communicator _communicator;

        IceShutdownHook(Communicator communicator){
            _communicator = communicator;
        }

        @Override
        public void run() {
            _communicator.destroy();
        }
    }
}
