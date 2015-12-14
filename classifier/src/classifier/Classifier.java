package classifier;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Classifier {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			List<Sketch> templates = TemplateMatcher.CreateTemplates(".");
			ArrayList<List<Sketch>> sketchesByNumberChinese = new ArrayList<List<Sketch>>();
			ArrayList<List<Sketch>> sketchesByNumberHindi = new ArrayList<List<Sketch>>();
			ArrayList<List<Sketch>> folds = new ArrayList<List<Sketch>>();
			int[][] confusionMatrix = new int[18][18];
			
			//int k = 0;
			int m = 0;
			for (int i = 0; i < 9; i++) {
				for (int k = 0; k < 3; k++) {
					sketchesByNumberChinese.add(new ArrayList<Sketch>());
					sketchesByNumberHindi.add(new ArrayList<Sketch>());
					for (int j = 0; j < 10; j++) {
						sketchesByNumberChinese.get(i).add(templates.get(90*k + 10*i + j));
					}
					for (int j = 0; j < 10; j++) {
						sketchesByNumberHindi.get(i).add(templates.get(270 + 90*k + 10*i + j));
					}
				}				
			}
			for (int i = 0; i < 10; i++) {
				folds.add(new ArrayList<Sketch>());
				for (int j = 0; j < 9; j++) {
					folds.get(i).add(sketchesByNumberChinese.get(j).get(m));
					folds.get(i).add(sketchesByNumberChinese.get(j).get(m+1));
					folds.get(i).add(sketchesByNumberChinese.get(j).get(m+2));					
				}
				for (int j = 0; j < 9; j++) {
					folds.get(i).add(sketchesByNumberHindi.get(j).get(m));
					folds.get(i).add(sketchesByNumberHindi.get(j).get(m+1));
					folds.get(i).add(sketchesByNumberHindi.get(j).get(m+2));					
				}
				m += 3;
			}
			TemplateMatcher tm = new TemplateMatcher(".");
			for (int i = 0; i < folds.size(); i++) {
				tm.myTemplates.clear();
				for (int j = 0; j < folds.size(); j++) {
					if(i != j)
						tm.myTemplates.addAll(folds.get(j));
				}
				for (int j = 0; j < folds.get(i).size(); j++) {
					Sketch input = folds.get(i).get(j);
					Pair<Integer, Integer> result = tm.Classify(input);
					int row = ((input.getLang())*9) + (input.getLabel() - 1);
					int col = ((result.getValue())*9) + (result.getKey() - 1);
					confusionMatrix[row][col]++;
				}
			}
			
			for (int i = 0; i < confusionMatrix.length; i++) {
				for (int j = 0; j < confusionMatrix[i].length; j++) {
					System.out.print(new DecimalFormat("#.##").format((double)((double)confusionMatrix[i][j]/30.00)) + "\t");
				}
				System.out.println("");
			}
			
			//System.out.println(confusionMatrix);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
