import java.util.Collection;
import java.util.HashMap;

public class GeneticAlgo {

	float duration;
	Timer timer;


	public GeneticAlgo(float duration) {
		this.duration = duration;
		this.timer = new Timer(duration);
	}
	
	/**
	 * Performs complete genetic algorithm, to be called from main
	 * 
	 * @param population (uninitialized)
	 * @return best individual
	 */
	public Individual GA(HashMap<Integer, Individual> population){
		HashMap<Integer, Individual> newPopulation = new HashMap<Integer, Individual>();
		float bestScore = 0;
		int bestKey = 0;
		timer.run();
//		for(int i = 0; i < population.size(); i++) {
//			population.get(i).setFitness(puzzleOneFitness(population.get(i)));
//		} 
		do {
			Random rand = new Random();
			for(int i = 0; i < population.size(); i++) {
				/*TODO: randomly select 2 members of population
				 * 	-make child
				 * 	-maybe mutate?
				 * 	-add child to new population
				 */
				Individual x = population.get(rand.nextInt(population.size()));
				Individual y = population.get(rand.nextInt(population.size()));
				Float[][] childData = reproductionFunction(x.getData(), y.getData(), 0.05f);
				newPopulation.put(i, new Individual(childData));
				newPopulation.get(i).setFitness(puzzleOneFitness(newPopulation.get(i)));
				if(newPopulation.get(i).getFitness() > bestScore) {
					bestScore = newPopulation.get(i).getFitness();
					bestKey = i;
				}
			}
		} while (!timer.finished());
		return newPopulation.get(bestKey);	//return best member of new population
	}
	
	/**
	 * Calculates the fitness of each individual
	 * 
	 * @param puzzleOption 1 for puzzle 1 or 2 for puzzle 2
	 * @param bin1         the product of numbers
	 * @param bin2         the sum of numbers
	 * @param bin3         the calculated range
	 * @param height       height of the tower
	 * @param totalCost    cost to make the tower
	 * @return the fitness score
	 */
	public static float fitnessFunction(int puzzleOption, float bin1, float bin2, float bin3, int height,
			int totalCost) {
		float fitnessScore = 0;
		if (puzzleOption == 1) {
			fitnessScore = bin1 + bin2 + bin3;
		} else if (puzzleOption == 2) {
			fitnessScore = (float) (10 + Math.pow(height, 2) - totalCost);
		}
		return fitnessScore;
	}


	/**
	 * Calculates the product of numbers in bin 1
	 * @param bin1 numbers to be multiplied
	 * @return the product of the bin
	 */
	public static float calculateBin1(float[][] bin1) {
		float product = 1;
        for (int i = 0; i < bin1[0].length; i++) {
        	product *= bin1[0][i];
        }
        return product;
	}
	
	/**
	 * Calculate the sum of the numbers in bin 2
	 * @param bin2 the numbers to be added
	 * @return the sum of the bin
	 */
	public static float calculateBin2(float[][] bin2) {
		float sum = 0;
        for (int i = 0; i < bin2[1].length; i++) {
        	sum += bin2[1][i];
        }
        return sum;
	}
	
	/**
	 * Calculates the range of the numbers in bin 3
	 * @param bin3 the numbers to get the range
	 * @return the range of the bin
	 */
	public static float calculateBin3(float[][] bin3) {
		float max = bin3[2][0];
        float min = bin3[2][0];
        for (int i = 0; i < bin3[2].length; i++) {
            if (bin3[2][i] > max) {
                max = bin3[2][i];
            } else if (bin3[2][i] < min) {
                min = bin3[2][i];
            }
        }
        return max - min;
	}
	
	/**
	 * Gets the height of the tower
	 * @param tower the constructed tower
	 * @return the height
	 */
	public static int getHeight(HashMap<Integer, String[]> tower) {		
		return tower.size();
	}
	
	/**
	 * Gets the total cost of building the tower
	 * @param tower the constructed tower
	 * @return the total cost of the tower
	 */
	public static int getCost(HashMap<Integer, String[]> tower) {
		int cost = 0;
		Collection<String[]> towerValues = tower.values();
		for (String[] pieceCost : towerValues) {
			cost += Integer.parseInt(pieceCost[3]);
		}
		return cost;

	
//	public static float puzzleOneFitness(Individual a) {
//		float score = 0;
//		for(int i = 0; i < 4; i++) {
//			Float[] bin = a.getData()[i];
//			if(i == 0) {score += multiplyBin(bin);}
//			if(i == 1) {score += sumBin(bin);}
//			if(i == 2) {score += rangeBin(bin);}
//		}
//		return score;
//	}
//	
//	public static float multiplyBin(Float[] bin) {
//		float score = 1;
//		for(int i = 0; i < 10; i++) {
//			score = score * bin[i];
//		}
//		return score;
//	}
//	
//	public static float sumBin(Float[] bin) {
//		float score = 0;
//		for(int i = 0; i < 10; i++) {
//			score = score + bin[i];
//		}
//		return score;
//	}
//	
//	public static float rangeBin(Float[] bin) {
//		float max = bin[0];
//		float min = bin[0];
//		for(int i = 0; i < 10; i++) {
//			if(bin[i] > max) {max = bin[i];}
//			if(bin[i] < min) {min = bin[i];}
//		}
//		return max - min;
//	}
	
	/**
	 * Breed two parents to create a child
	 * 
	 * @param parent1
	 * @param parent2
	 * @param mutationPercent from 0.0 to 1.0, will determine how many genes we
	 *                        randomly swap once we breed
	 * @return child
	 */
	public static Float[][] reproductionFunction(Float[][] parent1, Float[][] parent2, float mutationPercent) {
		Float[][] child = new Float[4][10];
		Random rand = new Random();

		// Take the 0th and 2nd columns from the first parent and the 1st and 3rd from
		// the second
		for (int i = 0; i <= 3; i++) {
			if (i % 2 == 0) {
				child[i] = parent1[i];
			} else {
				child[i] = parent2[i];
			}
		}

		// Mutate the genes so they can cross buckets
		for (int i = 0; i < mutationPercent * 40; i++) {
			int randBin1 = rand.nextInt(4);
			int randValue1 = rand.nextInt(10);

			int randBin2 = rand.nextInt(4);
			int randValue2 = rand.nextInt(10);

			float temp = child[randBin1][randValue1];
			child[randBin1][randValue1] = child[randBin2][randValue2];
			child[randBin2][randValue2] = temp;
		}

		return child;

	}
}

