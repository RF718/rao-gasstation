package net.bigpoint.assessment;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

import java.util.Collection;
import java.util.List;

public class raoGasStation implements GasStation {
    /**
     * @param gasPump
     */
    @Override
    public void addGasPump(GasPump gasPump) {

    }

    /**
     * @return
     */
    @Override
    public Collection<GasPump> getGasPumps() {
        return List.of();
    }

    /**
     * @param gasType
     * @param v
     * @param v1
     * @return
     * @throws NotEnoughGasException
     * @throws GasTooExpensiveException
     */
    @Override
    public double buyGas(GasType gasType, double v, double v1) throws NotEnoughGasException, GasTooExpensiveException {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public double getRevenue() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public int getNumberOfSales() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public int getNumberOfCancellationsNoGas() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return 0;
    }

    /**
     * @param gasType
     * @return
     */
    @Override
    public double getPrice(GasType gasType) {
        return 0;
    }

    /**
     * @param gasType
     * @param v
     */
    @Override
    public void setPrice(GasType gasType, double v) {

    }
}
