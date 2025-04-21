/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Layer Class responsible for each layer of our Neural Network.
 */
public class Layer {
    public ArrayList<Neuron> neurons;   //List with the neurons of the Layer.
    public int aF;                      //Activation function that the layer implements.

    /**
     * Constractor for non-input layer.
     * @param inputNeurons Int number. The number of input neurons of the layer.
     * @param layerNeurons Int number. The number of neurons in the layer.
     * @param aF Int number that indicates the activantion functions that layer implements.
     */
    public Layer(int inputNeurons , int layerNeurons,int aF){
        this.neurons=new ArrayList<Neuron>();
        this.aF=aF;

        for(int index=0;index<layerNeurons;index++){
            ArrayList<Double>randomWeights=new ArrayList<Double>();

            for(int index1=0;index1<inputNeurons;index1++)randomWeights.add(ThreadLocalRandom.current().nextDouble(-1,1));

            neurons.add(new Neuron(randomWeights,ThreadLocalRandom.current().nextDouble(-1,1)));
        }
    }

    /**
     * Constractor for a input layer.
     * @param input ArrayList.
     */
    public Layer(ArrayList<Double> input){
        this.neurons=new ArrayList<Neuron>();

        for(int index=0;index<input.size();index++)neurons.add(new Neuron(input.get(index)));
    }

    /**
     * Responsible for printing the state of each neuron of a layer.
     */
    public void printState(){
        for(int index=0;index<this.neurons.size();index++)neurons.get(index).printState();
    }
}
