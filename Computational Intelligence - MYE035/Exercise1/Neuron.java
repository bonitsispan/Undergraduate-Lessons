/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

import java.util.ArrayList;

/**
 * This class is responsible for a neuron of a layer.
*/
public class Neuron{
    public ArrayList<Double> weights;              //ArrayList with the weights for each link that ends in a neuron.
    public ArrayList<Double> partialDerivatives;   //ArrayList with partial derivatives of each weight of a neuron.
    Double weightBias,outputY,delta,sumInputU,partialDerivativesBias;  //Bias weight, output y of the neuron , delta of the neuron , total input u of the neuron, partial derivative for bias.

    /**
     * Constractor for non-input neuron.
     * @param weights ArrayList<Double> with weights.
     * @param weightBias Double number. The bias of the neuron.
     */
    public Neuron(ArrayList<Double>weights,double weightBias){
        initialWeights(weights);
        this.outputY=0.0;
        this.delta=0.0;
        this.weightBias=weightBias;
        this.sumInputU=0.0;
        this.partialDerivativesBias=0.0;
    }

    /**
     * Constractor for input neurons.
     * @param input Double number. The input of the neuron.
     */
    public Neuron(Double input){
        this.outputY=input;
        this.delta=0.0;
        this.weightBias=-1.0;
        this.delta=-1.0;
        this.sumInputU=0.0;
        this.partialDerivativesBias=0.0;
    }

    /**
     * This mehtod is responsible for constracting the list with weights and the list with partial derivatives.
     * @param weights ArrayList with double that contains random weights.
     */
    public void initialWeights(ArrayList<Double>weights){
        this.weights=new ArrayList<Double>();
        this.partialDerivatives=new ArrayList<Double>();

        for(int index=0;index<weights.size();index++)this.weights.add(weights.get(index));

        for(int index=0;index<weights.size();index++)this.partialDerivatives.add(0.0);
    }

    /**
     * Setter for the outputY variable.
     * @param input Double number.
     */
    public void setInput(double input){
        this.outputY=input;
    }

    /**
     * Getter for the weights.
     * @return ArrayList<Double>. The list with weights.
     */
    public ArrayList<Double> getWeights(){
        return this.weights;
    }

    /**
     * This method is responsible for setting all the elements of partial derivatives list to zero.
     */
    public void restartPartialDerivatives(){
        for(int index=0;index<this.partialDerivatives.size();index++)this.partialDerivatives.set(index,0.0);
        this.partialDerivativesBias=0.0;
    }

    /**
     * Print the state of Neural Network.
     */
    public void printState(){
        System.out.println("---------------------------------------------------");
        System.out.println("The bias of the neuron is : " + this.weightBias);
        System.out.println("The outputY of the neuron is : "+ this.outputY);System.out.println();

        if(this.weights!=null)for(int index=0;index<this.weights.size();index++)System.out.println("The weight from neuron : "+ index +" : "+weights.get(index));
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println();
    }
}
