/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

import java.util.ArrayList;
/**
 * This class implements a Neural Network.
 */
public class NeuralNetwork {
    ActivationFunctions activationFunction;
    ArrayList<Double>inputList;
    Double error;
    Layer[] layers;

    /**
     * Constractor For Neural Network class.
     * @param H1 Int number. The number of neurons of first hidden layer.
     * @param H2 Int number. The number of neurons of second hidden layer.
     * @param H3 Int number. The number of neurons of third hidden layer.
     * @param aF1 Int Activation Function.  Number that indicates the activation function that first hidden layer implements.
     * @param aF2 Int Activation Function.  Number that indicates the activation function that second hidden layer implemets.
     * @param aF3 Int Actiavation Function. Number that indicates the activation function that third hidden layer implements.
     * @param aFO Int Activation Function.  Number that indicates the activation function that the output layer implements.
     */
    public NeuralNetwork(int H1,int H2,int H3,int aF1,int aF2,int aF3,int aFO){
        this.activationFunction=new ActivationFunctions();
        this.inputList=new ArrayList<Double>();
        this.inputList.add(0.0);this.inputList.add(0.0);
        this.layers=new Layer[5];
        this.error=0.0;
        NeuralNetworkManager(H1,H2,H3,aF1,aF2,aF3,aFO);
    }


    /**
     * This method is used to calculate the result of a activation function (aF).
     * @param input Int number that indicates the activation function.
     * @param sumInputU Double number. The total input u of the activation function.
     * @return Double number. The result of activation function.
     */
    public double getActivationFunction(int aF,double sumInputU){
        switch(aF){
            case(1):
                return activationFunction.logistic(sumInputU);
            case(2):
                return activationFunction.tan(sumInputU);
            case(3):
                return activationFunction.RectifiedLinear(sumInputU);
            default:
                return activationFunction.linear(sumInputU);
        }
    }

     /**
      * This method is used to calculate the result of the derivatice of a activation function (aF).
      * @param aF Int number that indicates the activation function.
      * @param sumInputU Double number. The total input u of the activation function.
      * @return Double number. The result of the derivative of the activation function.
      */
    public double getActivationFunctionDerivative(int aF,double sumInputU){
        switch(aF){
            case(1):
                return activationFunction.logisticDerivative(sumInputU);
            case(2):
                return activationFunction.tanDerivative(sumInputU);
            case(3):
                return activationFunction.RectifiedLinearDerivative(sumInputU);
            default:
                return activationFunction.linearDerivative(sumInputU);
        }
    }

    /**
     * This method is used to change the input of the input Layer.
     * @param input ArrayList that contains the new inputs.
     */
    private void changeInputLayer(ArrayList<Double> input){
        for(int index=0;index<layers[0].neurons.size();index++)
            layers[0].neurons.get(index).setInput(input.get(index));
    }

    /**
     * Responsible for creating a new layer Object.
     * @param H1 Int number H1. Number of neurons in the first hidden layer.
     * @param H2 Int number H2. Number of neurons in the second hidden layer.
     * @param H3 Int number H3. Number of neurons in the third hidden layer.
     * @param aF1 Int number aF1. Number that indicates the activation function that First Hidden layer implements.
     * @param aF2 Int number aF2. Number that indicates the activation function that Second Hidden layer implements.
     * @param aF3 Int number aF3. Number that indicates the activation function that Third Hidden layer implements.
     * @param aFO Int number aFO. Number that indicates teh activation function that output layer implements.
     */
    private void NeuralNetworkManager(int H1,int H2,int H3,int aF1, int aF2 , int aF3,int aFO){
        layers[0]=new Layer(this.inputList);   //input layer.
        layers[1]=new Layer(2,H1,aF1);         //First H1 layer.
        layers[2]=new Layer(H1,H2,aF2);        //Second H2 layer.
        layers[3]=new Layer(H2,H3,aF3);        //Third H3 layer.
        layers[4]=new Layer(H3,3,aFO);         //output layer.
    }

    /**
     * This method implements the forward pass of backpropagation method.
     * @param input ArrayList<Double> with inputs of the neurons.
     */
    private void forwardPass(ArrayList<Double> input){
        changeInputLayer(input);    //Change the inputs of input layer.
        for(int index1=1;index1<layers.length;index1++){    //For each layer index1 .
           for(int index2=0;index2<layers[index1].neurons.size();index2++){   //For each neuron index2 of layer index.
                double sumInputU=0.0;

                for(int index3=0;index3<layers[index1-1].neurons.size();index3++)   //Calculate the sumInputU .
                    sumInputU+=layers[index1-1].neurons.get(index3).outputY * layers[index1].neurons.get(index2).weights.get(index3);

                sumInputU+=layers[index1].neurons.get(index2).weightBias;   //Add bias weight value to the sumInputU.
                layers[index1].neurons.get(index2).sumInputU=sumInputU;     //Change the sumInputU(u) of the index2 neuron in the layer index.
                layers[index1].neurons.get(index2).outputY=getActivationFunction(layers[index1].aF, sumInputU);   //calculate the outputY of the neuron.
           }
        }
    }

    /**
     * This method implements the reverse pass for the output layer.
     * @param expectedOutput ArrayList<Double> with expected output of the neurons.
     */
    private void backPropOutputLayer(ArrayList<Double> expectedOutput){
        for(int index=0;index<layers[4].neurons.size();index++){    //for every output neuron of the network.
            double subtraction=layers[4].neurons.get(index).outputY-expectedOutput.get(index);    //subtraction (ouput - expectedOuput).
            this.error+=subtraction*subtraction;    //caluclate the error.
            double delta=getActivationFunctionDerivative(layers[4].aF,layers[4].neurons.get(index).sumInputU)*subtraction;    //delta = g'(u)*(outputY-expectedOutput).

            layers[4].neurons.get(index).delta=delta;   //set the delta values of the neuron.

            for(int index1=0;index1<layers[4].neurons.get(index).weights.size();index1++){    //for every weight of the neuron.
                double productDeltaOutputY=delta*layers[3].neurons.get(index1).outputY;       //calulate the productDeltaOutputY as delta * outputY (outputY of the neuron in the previous layer).
                layers[4].neurons.get(index).partialDerivatives.set(index1,layers[4].neurons.get(index).partialDerivatives.get(index1)+productDeltaOutputY); //Update the partial derivative list.
            }
            layers[4].neurons.get(index).partialDerivativesBias+=delta; //Update the partial derivative for bias.
        }
    }


    /**
     * This method implements the reverse pass of backpropagation method.
     * @param expectedOutput ArrayList<Double> with the expected ouptuts of the neurons.
     */
    private void backProp(ArrayList<Double> expectedOutput){
        backPropOutputLayer(expectedOutput);    //first reverse pass for the output layer.

        for(int index=3;index>0;index--){       //for every layer of the other three hidden layers.
            for(int index1=0;index1<layers[index].neurons.size();index1++){   //for every neuron of the hidden layer.
                double derivativeInput=getActivationFunctionDerivative(layers[index].aF,layers[index].neurons.get(index1).sumInputU);   //g'(input) result.
                double sumWByDelta=sumWByDelta(index1,index+1);   //calculate the error of the layer+1.
                double delta=sumWByDelta*derivativeInput;         //calculate the delta.

                layers[index].neurons.get(index1).delta=delta;    //set the delta of that neuron.

                for(int index2=0;index2<layers[index].neurons.get(index1).weights.size();index2++){
                    double previousOutput=layers[index-1].neurons.get(index2).outputY;   //the ouput of neuron from one layer before.
                    double productDeltaOutputY=delta*previousOutput;                     //calculate delta * previous output.
                    layers[index].neurons.get(index1).partialDerivatives.set(index2, layers[index].neurons.get(index1).partialDerivatives.get(index2)+productDeltaOutputY); //Update partial derivative list.
                }
                layers[index].neurons.get(index1).partialDerivativesBias+=delta;         //Update the partial derivative for bias.
            }
        }
    }

    /**
     * This method is responsible of updating the weights of a neural network.
     * @param learningRate Double number. The learningRate of the neural network.
     */
    public void updateWeights(double learningRate){
        for(int index=4;index>0;index--){   //for each layer.
            for(int index1=0;index1<layers[index].neurons.size();index1++){   //for each neuron of the index layer.
                for(int index2=0;index2<layers[index].neurons.get(index1).weights.size();index2++){   //for each weight of the neuron
                    layers[index].neurons.get(index1).weights.set(index2,layers[index].neurons.get(index1).weights.get(index2)-learningRate*layers[index].neurons.get(index1).partialDerivatives.get(index2));    //w(t+1) = w(t) - learningRate * partial derivative
                }
                layers[index].neurons.get(index1).weightBias-=learningRate*layers[index].neurons.get(index1).partialDerivativesBias;    //Update the weight for bias.
            }
        }
    }

    /**
     * This method is used for restarting (set all the elements of the list to zero) partial derivative list.
     */
    public void restartPartialDerivatives(){    //for every layer.
        for(int index=4;index>0;index--){
            for(int index1=0;index1<layers[index].neurons.size();index1++)        //for every neuron of the layer.
                layers[index].neurons.get(index1).restartPartialDerivatives();    //Restart the partial derivative of the neuron.

        }
    }

    /**
     * This method is responsible for implementing the backpropagation method for one example.
     * @param input The ArrayList<Double> with inputs of the neural network.
     * @param expectedOutput The ArrayList<Double> with expected outputs of the neural network.
     * @return Double number. The error of neural network .
     */
    public double training(ArrayList<Double> input,ArrayList<Double> expectedOutput){
        this.error=0.0;
        forwardPass(input);
        backProp(expectedOutput);
        return this.error/2;
    }

    /**
     * This method is responsible for calculating the error of neural network for one example.
     * @param input ArrayList<Double> with inputs of the neural network.
     * @param expectedOutput ArrayList<Double> with exepected Ouputs of the neural network.
     * @return Double number. The error.
     */
    public double generalizationError(ArrayList<Double> input,ArrayList<Double> expectedOutput){
        this.error=0.0;
        forwardPass(input);   //forward pass for the given input

        for(int index=0;index<layers[4].neurons.size();index++){    //for every output neuron of the network.
            double subtraction=layers[4].neurons.get(index).outputY-expectedOutput.get(index); //subtraction (outputY - expectedOutput) for the neuron.
            this.error+=subtraction*subtraction;    //Square of error.
        }

        return this.error/2;
    }

    /**
     * This method is responsible for calculating the sum of the product of weights and delta.
     * @param indexNeuron Int number. The number of the neuron of a layer.
     * @param indexLayer Int number. The number of the layer.
     * @return
     */
    private double sumWByDelta(int indexNeuron,int indexLayer){
        double sumWByDelta=0.0;
        for(int index=0;index<layers[indexLayer].neurons.size();index++){
            Neuron currentNeuron=layers[indexLayer].neurons.get(index);
            sumWByDelta+=currentNeuron.weights.get(indexNeuron)*currentNeuron.delta;    //Mutiply the weight from the given neuron with the delta of the neuron and add it to the sum.
        }
        return sumWByDelta;
    }

    /**
     * Responsible for printing the layer state.
     */
    public void printLayer(){
        for(int index=0;index<layers.length;index++){
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println();System.out.println("The layer : "+ index);System.out.println();
            layers[index].printState();
            System.out.println();System.out.println();
            System.out.println("-----------------------------------------------------------------------------");
        }
    }
}
