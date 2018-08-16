package team.benchem;

import com.zeroc.Ice.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import team.benchem.communication.impl.JsonServiceCenterImpl;

/**
 * @ClassName Program
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-15 11:54
 * @Version 1.0
 **/
@SpringBootApplication
public class Program {
    public static void main(String... args){

        try(Communicator communicator = Util.initialize(args)) {

            Runtime.getRuntime().addShutdownHook(new IceShutdownHook(communicator));

            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("LonntecRPC", "default -h 0.0.0.0 -p 9090");
            adapter.add(new JsonServiceCenterImpl(), Util.stringToIdentity("LonntecRPC"));
            adapter.activate();

            SpringApplication.run(Program.class, args);
            communicator.waitForShutdown();
        }
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
