package team.benchem.communication;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

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

        communicator = Util.initialize(args.getSourceArgs());
        Runtime.getRuntime().addShutdownHook(new IceShutdownHook(communicator));
        ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("LonntecRPC", "default -h 0.0.0.0 -p 9090");
        adapter.add(serviceCenter, Util.stringToIdentity("LonntecRPC"));
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
