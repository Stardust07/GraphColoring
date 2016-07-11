import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


public class Main {
	public static int maxColor;
	public static int n, k, f;
	public static int[] solution;
	public static boolean[][] graph;
	public static int[][] conflicts;
	public static int[][] tabuTable;
	public static int iteration = 0, tabuStep;
	public static int node, color, reduce;
	public static String[] instances = {"DSJC125.1","DSJC125.9","DSJC250.5","DSJC500.1","DSJC500.5"};
	public static int bestInHistory;
	public static int time = 0;
	final static int THRESHOLD = 3;
	
	public static void init() {
		graph = new boolean[n][n];
		solution = new int[n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				graph[i][j] = false;
			}
			solution[i] = n;
		}
		maxColor = 0;
		tabuStep = 20;
    }
	
	public static void readInstance(String fileName){
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
            	if(tempString.startsWith("p edge")) {
            		String strings[] = tempString.split(" ");
            		n = Integer.parseInt(strings[2]);
            		init();
            	} else if(tempString.startsWith("e")) {
            		String strings[] = tempString.split(" ");
            		graph[Integer.parseInt(strings[1]) - 1][Integer.parseInt(strings[2]) - 1] =
            				graph[Integer.parseInt(strings[2]) - 1][Integer.parseInt(strings[1]) - 1]
            						= true;
            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	public static void findMaxColor() {
		for(int i = 0; i < n; i++) {
			int minColor = 0;
			for(; minColor < n; minColor++) {
				int j = 0;
				for(; j < n; j++) {
					if(graph[i][j] && solution[j] == minColor) {
						break;
					}
				}
				if(j == n) {
					break;
				}
			}
			if(minColor < n) {
				solution[i] = minColor;	
				if(minColor > maxColor) {
					maxColor = minColor;
				}
			}
		}
		maxColor++;      //需要的颜色数
		k = maxColor - 1;
	}
	
	public static boolean check() {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(graph[i][j] && solution[i] == solution[j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void initSolution(){
		solution = new int[n];
		Random random = new Random();
		f = 0;//冲突值
		for(int i = 0; i < n; i++) {
			solution[i] = k;
		}
//		
		for(int i = 0; i < n; i++) {
			solution[i] = random.nextInt(k);
			for(int j = 0; j < i; j++) {
				if(graph[i][j] && solution[i] == solution[j]) {
					f++;
				}
			}
		}
	}
	
	public static void initConflicts() {
		tabuTable = new int[n][k];
		conflicts = new int[n][k];
		for(int i = 0; i < n; i++) {
			for(int c = 0; c < k; c++) {
				tabuTable[i][c] = 0;
				for(int j = 0; j < n; j++) {
					if(graph[i][j] && solution[j] == c) {
						conflicts[i][c]++;
					}
				}
			}
		}
	}
	
	public static void updateConflicts() {
		for(int i = 0; i < n; i++) {
			if(graph[node][i]) {
				conflicts[i][color]++;
				conflicts[i][solution[node]]--;
			}
		}
	}
	
	public static boolean findOperation() {
		int numberOfFound = 0;
		reduce = Integer.MAX_VALUE;
		int tabuNode = 0, tabuColor = 0, tabuReduce = Integer.MAX_VALUE, tabuFound = 0;
		for(int i = 0; i < n; i++) {
			if(conflicts[i][solution[i]] == 0) { //TODO
				continue;
			}
			for(int c = 0; c < k; c++) {
				if(c == solution[i]) {
					continue;
				}
				if(iteration <= tabuTable[i][c]) {   //被禁
					if(conflicts[i][c] - conflicts[i][solution[i]] < tabuReduce) {
						tabuFound = 1;
						tabuReduce = conflicts[i][c] - conflicts[i][solution[i]];
						tabuNode = i;
						tabuColor = c;
					} else if(conflicts[i][c] - conflicts[i][solution[i]] == tabuReduce) {
						tabuFound++;
						Random random = new Random();
						if(random.nextInt(tabuFound) < 1) {  //1/numberOfFound的概率取新的
							tabuNode = i;
							tabuColor = c;
						}
					}
				} else if(conflicts[i][c] - conflicts[i][solution[i]] < reduce) {
					numberOfFound = 1;
					reduce = conflicts[i][c] - conflicts[i][solution[i]];
					node = i;
					color = c;
				} else if(conflicts[i][c] - conflicts[i][solution[i]] == reduce) {
					numberOfFound++;
					Random random = new Random();
					if(random.nextInt(numberOfFound) < 1) {  //1/numberOfFound的概率取新的
						node = i;
						color = c;
					}
				}
			}
		}
		if(tabuReduce < reduce && tabuReduce + f < bestInHistory) {
			node = tabuNode;
			color = tabuColor;
			reduce = tabuReduce;
			tabuTable[node][color] = iteration;
		}
//		System.out.println(iteration+":\t"+"node:"+node + "\t"+"color:"+color + "\t"+"reduce:"+reduce);
		if(numberOfFound == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean judge(){  //判断k种颜色是否可行
		initSolution();
		initConflicts();
		bestInHistory = f;
		iteration = 0;
		while(f > 0) {
			if(time > 10000 * n) {
//				System.out.println(time+":\t"+f+"\t");
//				return false;
			}
			iteration++;
			if(findOperation()){
				//System.out.println(node+"\t"+color+"\t"+reduce);
				System.out.println(iteration+":node"+node+"("+solution[node]+"to"+color+")\t"+(f+reduce)+"\t");
				updateConflicts();
				tabuTable[node][solution[node]] = iteration + tabuStep;
				solution[node] = color;
				f += reduce;
				if(f < bestInHistory) {
					time = 0;
					bestInHistory = f;
				} else {
					time++;
				}
			} else {
				System.out.println(k+"no");
				return false;
			}
		}
		
		System.out.print(k+"yes"+"\t"); System.out.println(check());
		return true;
	}
	
	public static void main(String[] args){
		readInstance("D:\\qym\\workspace\\GraphColoring\\"+instances[4]+".col.txt");
		findMaxColor(); //求可以找到一个解的颜色数
		long time = System.currentTimeMillis(); 
		k = 49;
		judge();
//		while(k > 0) {
//			if(!judge()) {
//				break;
//			}
//			k--;
//		}
//		k++;
//		System.out.println("greedy:" + maxColor);
		System.out.println("final k:" + k);
		System.out.println("time:" + (System.currentTimeMillis() - time) / 1000);
	}
}
