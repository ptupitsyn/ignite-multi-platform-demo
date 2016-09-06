import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryBasicIdMapper;
import org.apache.ignite.binary.BinaryBasicNameMapper;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Demo {
    public static void main(String[] args) {
        BinaryConfiguration binCfg = new BinaryConfiguration();

        binCfg.setIdMapper(new BinaryBasicIdMapper());
        binCfg.setNameMapper(new BinaryBasicNameMapper());

        IgniteConfiguration cfg = new IgniteConfiguration().setBinaryConfiguration(binCfg);

        Ignition.start(cfg);
    }
}
