import org.junit.Test;
import org.junit.runner.RunWith;
import org.ntjr.KafkaApplication;
import org.ntjr.provider.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaApplication.class)
public class BaseTest {
    @Autowired
    private KafkaSender msgProducer;

    @Test
    public void test() throws Exception {
        msgProducer.send();
    }

}
