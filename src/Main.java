
public class Main {
	public static int k;
	public static int n;
	public static boolean[][] graph;
	
	public static void readInstance(){
		n = 5;
		graph = new boolean[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				graph[i][j] = false;
			}
		}
		graph[0][1] = graph[1][0] = true;
		graph[1][2] = graph[2][1] = true;
		graph[2][3] = graph[3][2] = true;
		graph[3][4] = graph[4][3] = true;
		graph[4][0] = graph[0][4] = true;
		graph[1][3] = graph[3][1] = true;
		k = n;
	}
	
	public static void initSolution(){
		for(int i = 0; i < n; i++) {
			
		}
		return;
	}
	public static void main(String[] args){
		readInstance();
		initSolution();
		System.out.println("yes");
	}
}
