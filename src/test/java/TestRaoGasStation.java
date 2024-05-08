import static org.junit.jupiter.api.Assertions.*;

import net.bigpoint.assessment.RaoGasStation;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class RaoGasStationTest {

    private RaoGasStation station;

    @BeforeEach
    void setup() {
        station = new RaoGasStation(10);
    }

    @Test
    void testAddGasPump() {
        GasPump pump = new GasPump(GasType.REGULAR, 1000.0);
        station.addGasPump(pump);
        Collection<GasPump> pumps = station.getGasPumps();
        assertTrue(pumps.contains(pump), "Gas pump should be added");
    }

    @Test
    void testBuyGasNormal() throws NotEnoughGasException, GasTooExpensiveException {
        GasPump pump = new GasPump(GasType.REGULAR, 500.0);
        station.addGasPump(pump);
        station.setPrice(GasType.REGULAR, 1.50);
        double price = station.buyGas(GasType.REGULAR, 100.0, 2.00);
        assertEquals(150.0, price, "Price should match expected calculation");
    }

    @Test
    void testBuyGasNotEnoughGasException() {
        GasPump pump = new GasPump(GasType.REGULAR, 100.0);
        station.addGasPump(pump);
        station.setPrice(GasType.REGULAR, 1.50);
        assertThrows(NotEnoughGasException.class, () -> {
            station.buyGas(GasType.REGULAR, 200.0, 2.00);
        }, "Should throw NotEnoughGasException because there is not enough gas");
    }

    @Test
    void testBuyGasTooExpensiveException() {
        GasPump pump = new GasPump(GasType.REGULAR, 1000.0);
        station.addGasPump(pump);
        station.setPrice(GasType.REGULAR, 3.00);
        assertThrows(GasTooExpensiveException.class, () -> {
            station.buyGas(GasType.REGULAR, 100.0, 2.00);
        }, "Should throw GasTooExpensiveException because the price is too high");
    }

    @Test
    void testSetAndGetPrice() {
        station.setPrice(GasType.REGULAR, 1.75);
        assertEquals(1.75, station.getPrice(GasType.REGULAR), "The price should be correctly set and gotten");
    }

    @Test
    void testConcurrentBuyGas() throws NotEnoughGasException, GasTooExpensiveException {
        int threadCount = 105;
        station = new RaoGasStation(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        GasPump[] pumps = new GasPump[]{new GasPump(GasType.REGULAR, 1000.0),
                                        new GasPump(GasType.REGULAR, 500.0),
                                        new GasPump(GasType.REGULAR, 200.0),
                                        new GasPump(GasType.DIESEL, 1000.0),
                                        new GasPump(GasType.DIESEL, 500.0),
                                        new GasPump(GasType.DIESEL, 200.0),
                                        new GasPump(GasType.SUPER, 1000.0),
                                        new GasPump(GasType.SUPER, 500.0),
                                        new GasPump(GasType.SUPER, 200.0),};
        for (GasPump pump : pumps) {station.addGasPump(pump);}
        station.setPrice(GasType.REGULAR, 1.50);
        station.setPrice(GasType.DIESEL, 1.00);
        station.setPrice(GasType.SUPER, 1.20);
        for(int i = 0; i < 105; i++){
            final int index = i;
            executor.submit(()->{
               try {
                   GasType type = GasType.values()[index%3];
                   double amountInLiter = index<99 ? 50.0 : 100.0;
                   double maxPrice = index < 102 ? 1.50 : 0.01;
                   station.buyGas(type, amountInLiter, maxPrice);
                   System.out.println("Buy gas with type: " + type );
               } catch (GasTooExpensiveException e) {
                   System.out.println("too expensive!");
               }catch (NotEnoughGasException e){
                   System.out.println("not enough gas!");
               }
               finally {
                   countDownLatch.countDown();
               }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
        //33*50*1.5+33*50*1.2+33*50+1.0
        assertEquals(6105.00,station.getRevenue(),"Revenue should match expected calculation");
        assertEquals(99,station.getNumberOfSales(),"Number of sales should match expected calculation");
        assertEquals(3,station.getNumberOfCancellationsNoGas(),"Number of cancellation no gas should match expected calculation");
        assertEquals(3,station.getNumberOfCancellationsTooExpensive(),"Number of cancellation too expensive should match expected calculation");
    }
}

