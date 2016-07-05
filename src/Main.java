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
	public static int node, color, reduce;
	public static String[] instances = {"DSJC125.1","DSJC125.9","DSJC250.5","DSJC500.1","DSJC500.5"};
	
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
    }
	
	public static void readInstance(String fileName){
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
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
                line++;
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
		maxColor++;
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
//			for(int j = 0; j < i; j++) {
//				if(graph[i][j] && solution[i] == solution[j]) {
//					f++;
//				}
//			}
		}
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < i; j++) {
				if(graph[i][j] && solution[i] == solution[j]) {
					f++;
				}
			}
		}
	}
	
	public static void initConflicts() {
		conflicts = new int[n][k];
		for(int i = 0; i < n; i++) {
			for(int c = 0; c < k; c++) {
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
		reduce = 0;
		for(int i = 0; i < n; i++) {
			for(int c = 0; c < k; c++) {
				if(conflicts[i][c] - conflicts[i][solution[i]] < reduce) {
					numberOfFound = 1;
					reduce = conflicts[i][c] - conflicts[i][solution[i]];
					node = i;
					color = c;
				} else if(reduce != 0 && conflicts[i][c] - conflicts[i][solution[i]] == reduce) {
					numberOfFound++;
					Random random = new Random();
					if(!(random.nextInt(numberOfFound) < 1)) {  //1/numberOfFound的概率取新的
						node = i;
						color = c;
					}
				}
			}
		}
		if(numberOfFound == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean judge(){  //判断k种颜色是否可行
		initSolution();
//		for(int i = 0; i < n; i++) {
//			System.out.println(solution[i]);
//		}
		initConflicts();
//		for(int i = 0; i < n; i++) {
//			for(int j = 0; j < k; j++) {
//				System.out.print(conflicts[i][j]+"\t");
//			}
//			System.out.println();
//		}
		while(f > 0) {
			if(findOperation()){
				//System.out.println(node+"\t"+color+"\t"+reduce);
				updateConflicts();
				solution[node] = color;
				f += reduce;
			} else {
				System.out.println(k+"no");
				return false;
			}
			
		}
		
		System.out.println(k+"yes");
//		for(int i = 0; i < n; i++) {
//			System.out.println(solution[i]);
//		}
		return true;
	}
	
	public static void main(String[] args){
		readInstance("F:\\"+instances[0]+".col.txt");
//		readInstance("F:\\1.txt");
		findMaxColor(); //求可以找到一个解的颜色数
		System.out.println(check());
		while(k > 0) {
			if(!judge()) {
				break;
			}
			k--;
		}
		k++;
		System.out.println("final k:" + k);
	}
}
