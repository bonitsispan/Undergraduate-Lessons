/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

/**
 * This class is responsible for providing all the activation functions that are needed.
 */
public class ActivationFunctions {
   /**
    * Linear activation Function
    * @param input Double number.
    * @return Double number of linear.
    */
    public double linear(double input){
        return input;
    }

  /**
   * Linear Derivative Function.
   * @param input Double number.
   * @return Double number of linear derivative.
   */
    public double linearDerivative(double input){
        return 1.0;
    }

    /**
     * logistic Activation Function.
     * @param input Double number.
     * @return Double number of logistic.
     */
    public double logistic(double input){
        return 1/(1+Math.exp(-input));
    }

    /**
     * logistic Derivative Function.
     * @param input Double number.
     * @return Double number of logistic derivative.
     */
    public double logisticDerivative(double input){
        double temp=logistic(input);
        return temp*(1-temp);
    }

    /**
     * Tan Derivative Function
     * @param input Double number.
     * @return Double number of tan.
     */
    public double tan(double input){
        return (Math.exp(input)-Math.exp(-input))/(Math.exp(input)+ Math.exp(-input));
    }

    /**
     * Tan derivative function.
     * @param input Double number.
     * @return Double number of tan derivative.
     */
    public double tanDerivative(double input){
        double temp=tan(input);
        return 1-(temp*temp);
    }

    /**
     * Relu (RectifiedLinear) function.
     * @param input Double number.
     * @return Double number of relu.
     */
    public double RectifiedLinear(double input){
        return Math.max(0, input);
    }

    /**
     * Derivative Relu function.
     * @param input Double number.
     * @return Double number of relu derivative.
     */
    public double RectifiedLinearDerivative(double input){
        if(input<0)return 0.0;
        return 1.0;
    }
}
