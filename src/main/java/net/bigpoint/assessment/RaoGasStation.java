package net.bigpoint.assessment;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Rao Fu
 *
 * BigPoint Test: GasStation
 */
public class RaoGasStation implements GasStation {
    //to create ThreadPool
    private final ExecutorService executorService;
    //save pumps
    private final ConcurrentMap<GasType,List<GasPump>> pumps = new ConcurrentHashMap<>();
    //save price per liter
    private final ConcurrentMap<GasType,Double> priceList = new ConcurrentHashMap<>();
    //save and update revenue
    private final AtomicReference<BigDecimal> totalRevenue = new AtomicReference<>(BigDecimal.ZERO);
    //save and update sales
    private final AtomicInteger numberOfSales = new AtomicInteger(0);
    //save and update cancellation because of no gas
    private final AtomicInteger numberOfCancellationNoGas = new AtomicInteger(0);
    //save and update cancellation because of too expensive
    private final AtomicInteger numberOfCancellationTooExpensive = new AtomicInteger(0);


    public RaoGasStation(int threadCount) {
        this.executorService = new ThreadPoolExecutor(
                                                    threadCount,
                                                    threadCount*2,
                                                    0L,
                                                    TimeUnit.MILLISECONDS,
                                                    new LinkedBlockingDeque<>()
                                                    );
    }
    public RaoGasStation(int threadCount,List<GasPump> pumpList) {
        this(threadCount);
        pumpList.forEach(this::addGasPump);
    }
    /**
     * @param pump
 *              the gas pump
     */
    @Override
    public void addGasPump(GasPump pump) {
        pumps.computeIfAbsent(pump.getGasType(),k -> new CopyOnWriteArrayList<>()).add(pump);
    }

    /**
     * @return all gas pumps, read only
     */
    @Override
    public Collection<GasPump> getGasPumps() {
        List<GasPump> allPumps = new ArrayList<>();
        pumps.forEach((k,v)->allPumps.addAll(v));
        return Collections.unmodifiableCollection(allPumps);
    }

    /**
     * Simulates a customer wanting to buy a specific amount of gas.
     *
     * @param type
     *            The type of gas the customer wants to buy
     * @param amountInLiters
     *            The amount of gas the customer wants to buy. Nothing less than this amount is acceptable!
     * @param maxPricePerLiter
     *            The maximum price the customer is willing to pay per liter
     * @return the price the customer has to pay for this transaction
     * @throws NotEnoughGasException
     *             Should be thrown in case not enough gas of this type can be provided
     *             by any single {@link GasPump}.
     * @throws GasTooExpensiveException
     *             Should be thrown if gas is not sold at the requested price (or any lower price)
     */
    @Override
    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
        Callable<Double> buyTask = () ->{

            if(maxPricePerLiter<getPrice(type)){
                numberOfCancellationTooExpensive.updateAndGet(current ->++current);
                throw new GasTooExpensiveException();
            }
            List<GasPump> pumpList = pumps.get(type);
            if(pumpList!=null){
                for(GasPump pump : pumpList){
                    if(pump.getRemainingAmount()>=amountInLiters){
                        pump.pumpGas(amountInLiters);
                        BigDecimal cost = BigDecimal.valueOf(maxPricePerLiter).multiply(BigDecimal.valueOf(amountInLiters)).setScale(2, RoundingMode.HALF_DOWN);
                        totalRevenue.updateAndGet(current->current.add(cost));
                        numberOfSales.updateAndGet(current ->++current);
                        return cost.doubleValue();
                    }
                }
            }
            numberOfCancellationNoGas.updateAndGet(current ->++current);
            throw new NotEnoughGasException();
        };
        Future<Double> buyResult = executorService.submit(buyTask);
        try {
            return buyResult.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NotEnoughGasException) {
                throw (NotEnoughGasException) cause;
            } else if (cause instanceof GasTooExpensiveException) {
                throw (GasTooExpensiveException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while buying gas", e);
        }
    }

    /**
     * @return the Revenue
     */
    @Override
    public double getRevenue() {
        return totalRevenue.get().doubleValue();
    }

    /**
     * @return the number of sales
     */
    @Override
    public int getNumberOfSales() {
        return numberOfSales.get();
    }

    /**
     * @return the number of cancellation because of no gas
     */
    @Override
    public int getNumberOfCancellationsNoGas() {
        return numberOfCancellationNoGas.get();
    }

    /**
     * @return the number of cancellation because of too expensive
     */
    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return numberOfCancellationTooExpensive.get();
    }

    /**
     * @param type
     *              the gas type
     * @return the price per liter
     */
    @Override
    public double getPrice(GasType type) {
        return priceList.getOrDefault(type,0.0);
    }

    /**
     * @param type
     *          the gas type
     * @param price
     *          the price per liter
     */
    @Override
    public void setPrice(GasType type, double price) {
        priceList.put(type,price);
    }
}
