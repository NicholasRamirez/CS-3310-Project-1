import java.util.*;
import java.math.*;

public class MatrixMultiplication
{
	public static int n = 0;
	public static double start, end = 0;
	
	public static void main(String args[]) 
	{
		Scanner s = new Scanner(System.in);
		
		
		System.out.print("Matrix size n (n x n): ");
		n = s.nextInt();
		int[][] a = new int[n][n];
	    int[][] b = new int[n][n];
	    int[][] c = new int[n][n];
	    

	    System.out.print("Press 1 to enter your own inputs. Any other number will result in a randomly generated matrix of size n x n: ");
		int choice = s.nextInt();
		if (choice == 1)
		{
			System.out.println("Enter the values for Matrix A: ");
			for (int i = 0; i < n; i++) 
			{
				for (int j = 0; j < n; j++) 
					a[i][j] = s.nextInt();
			}
			System.out.println("Enter the values for Matrix B: ");
			for (int i = 0; i < n; i++) 
			{
				for (int j = 0; j < n; j++) 
					b[i][j] = s.nextInt();
			}
			System.out.println();
		}
		else
		{
			System.out.println("Matrix A:"); //comment out this line when testing runtimes
			Random r = new Random();
			for (int i = 0; i < n; i++) //randomly generates digits between 9 and -9 into matrix A of size n x n
  			{
      			for (int j = 0; j < n; j++)
      			{		
        			a[i][j] = r.nextInt(19) - 10;
        			System.out.print(a[i][j] + "\t"); //comment out this line when testing runtimes
      			}
				System.out.println();  //comment out this line when testing runtimes
			}
			System.out.println();
			System.out.println("Matrix B:"); //comment out this line when testing runtimes
			for (int i = 0; i < n; i++) //randomly generates digits between 9 and -9 into matrix B of size n x n
  			{
      			for (int j = 0; j < n; j++)
      			{		
        			b[i][j] = r.nextInt(19) - 10;
        			System.out.print(b[i][j] + "\t"); //comment out this line when testing runtimes
      			}
				System.out.println();  //comment out this line when testing runtimes
			}
			System.out.println();
		}
		
				
		start = System.nanoTime(); //Calls classical or brute force function, outputs time in seconds and the resulting matrix
		c = classical(a, b);
	    end = System.nanoTime();    
	    System.out.println("Classical Run Time: " + (end-start) / 1000000000 + " Seconds");
		System.out.println("Classical | Matrix C:");  //comment out this line when testing runtimes
	    printMatrix(c);  //comment out this line when testing runtimes
		System.out.println();
	    
	    
	    start = System.nanoTime(); //Calls padded Native Divide and Conquer function, outputs time in seconds and the resulting matrix
	    c = paddedNDC(a, b);
	    end = System.nanoTime();
	    System.out.println("Native Divide and Conquer Run Time: " + (end-start) / 1000000000 + " Seconds");
		System.out.println("Native Divide and Conquer | Matrix C:");  //comment out this line when testing runtimes
	    printMatrix(c);  //comment out this line when testing runtimes
		System.out.println();
	    
	    
	    start = System.nanoTime(); //Calls padded Strassen function, outputs time in seconds and the resulting matrix
	    c = paddedStrassen(a, b);
	    end = System.nanoTime();
	    System.out.println("Strassen Run Time: " + (end-start) / 1000000000 + " Seconds");
		System.out.println("Strassen| Matrix C:");  //comment out this line when testing runtimes
	    printMatrix(c);	 //comment out this line when testing runtimes
		
		
		s.close();
	}
	//O(n^3) classical (brute force) algorithm
	public static int[][] classical(int[][] a, int[][] b) 
	{
		int[][] c = new int[n][n];
		for (int i = 0; i < n; i++) 
		{
			for (int j = 0; j < n; j++) 
			{
				for (int k = 0; k < n; k++) 
					c[i][j] += a[i][k] * b[k][j];
			}
		}
		return c;
	}
	//pads matrix A and B before calling the recursive D&C method
	//padding done through finding the max length then finding the closest 2^k that is greater and using that to expand the matrices to 
	//that size. Removes padding at the end.
	public static int[][] paddedNDC(int[][] A, int[][] B)
	{
		int pMax = Math.max(Math.max(A.length, A[0].length), B[0].length);
		int k = 0; 
		while ((1 << k) < pMax) 
			k++;
		k = 1 << k;	
		int[][] a = padMatrix(A, k);
		int[][] b = padMatrix(B, k); 
		int[][] c = new int[k][k];
		c = nativeDivConq(a, b, 0, 0, 0, 0, k);
		if (k != n) 
			c = removePad(c, A.length, B[0].length);
		return c;
	}
	//Divides A and B into quadrants and multiplies A and B recursively using multiplications of these quadrants
	//Calls addMatrices to add the parts together
	public static int[][] nativeDivConq(int[][] a, int[][] b, int rowA, int colA, int rowB, int colB, int size)
    {
        int[][] c = new int[size][size];
        if (size == 1)
            c[0][0] = a[rowA][colA] * b[rowB][colB];
        else
        {
            int newSize = size / 2;
            addMatrices(c, nativeDivConq(a, b, rowA, colA, rowB, colB, newSize), 
						nativeDivConq(a, b, rowA, colA + newSize, rowB + newSize, colB, newSize), 0, 0);
            addMatrices(c, nativeDivConq(a, b, rowA, colA, rowB, colB + newSize, newSize),
						nativeDivConq(a, b, rowA, colA + newSize, rowB + newSize, colB + newSize, newSize), 0, newSize);
            addMatrices(c, nativeDivConq(a, b, rowA + newSize, colA, rowB, colB, newSize),
						nativeDivConq(a, b, rowA + newSize, colA + newSize, rowB + newSize, colB, newSize), newSize, 0);
            addMatrices(c, nativeDivConq(a, b, rowA + newSize, colA, rowB, colB + newSize, newSize),
						nativeDivConq(a, b, rowA + newSize, colA + newSize, rowB + newSize, colB + newSize, newSize), newSize, newSize);
        }
        return c;
	}
	public static int[][] paddedStrassen(int[][] A, int[][] B)
	{
		int pMax = Math.max(Math.max(A.length, A[0].length), B[0].length);
		int k = 0; 
		while ((1 << k) < pMax) 
			k++;
		k = 1 << k;	
		int[][] a = padMatrix(A, k);
		int[][] b = padMatrix(B, k); 
		int[][] c = new int[k][k];
		c = strassen(a, b);
		if (k != n) 
			c = removePad(c, A.length, B[0].length);
		return c;
	}
	//Strassen algorithm splits A and B into quadrants then multiplying them recursively 7 times for P1-7
	//Then adds them together to produce the submatrcies of C and returning C to main
	//Uses same padding technique as the D&C
	public static int[][] strassen(int[][] a, int[][] b)
	{
		int k = a.length;
		int[][] c = new int[k][k];
		

		if (k == 1)
		{
        	c[0][0] = a[0][0] * b[0][0];
			return c;
		}
		

		int[][] a11 = new int[k / 2][k / 2];
		int[][] a12 = new int[k / 2][k / 2];
		int[][] a21 = new int[k / 2][k / 2];
		int[][] a22 = new int[k / 2][k / 2];
		int[][] b11 = new int[k / 2][k / 2];
		int[][] b12 = new int[k / 2][k / 2];
		int[][] b21 = new int[k / 2][k / 2];
		int[][] b22 = new int[k / 2][k / 2];


		split(a, a11, 0, 0);
		split(a, a12, 0, k / 2);
		split(a, a21, k / 2, 0);
		split(a, a22, k / 2, k / 2);
		split(b, b11, 0, 0);
		split(b, b12, 0, k / 2);
		split(b, b21, k / 2, 0);
		split(b, b22, k / 2, k / 2);


		int [][] P1 = strassen(add(a11, a22), add(b11, b22));
        int [][] P2 = strassen(add(a21, a22), b11);
        int [][] P3 = strassen(a11, sub(b12, b22));
        int [][] P4 = strassen(a22, sub(b21, b11));
        int [][] P5 = strassen(add(a11, a12), b22);
        int [][] P6 = strassen(sub(a21, a11), add(b11, b12));
        int [][] P7 = strassen(sub(a12, a22), add(b21, b22));
		int [][] C11 = add(sub(add(P1, P4), P5), P7);
        int [][] C12 = add(P3, P5);
        int [][] C21 = add(P2, P4);
		int [][] C22 = add(sub(add(P1, P3), P2), P6); 


		join(c, C11, 0, 0);   
		join(c, C12, 0, k / 2);
        join(c, C21, k / 2, 0);
        join(c, C22, k / 2, k / 2);
		return c;
	}
	//adds the product matrices together to produce the submatrices of C
	private static void addMatrices(int[][] c, int[][] a, int[][] b, int rowC, int colC)
    {
		int n = a.length;
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
                c[i + rowC][j + colC] = a[i][j] + b[i][j];
        }
    }
	//transfers the data of the original matrix into the expanded or padded matrix while keeping the same indexes
	public static int[][] padMatrix(int[][] x, int k)
	{
		int[][] r = new int[k][k];
		if(x.length == k && x[0].length == k)
            return x;
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
                r[i][j] = x[i][j];
        }
		return r;
	}
	//returns matrix to original size n x n or just returns c if no padding
	private static int[][] removePad(int[][] c, int aRow, int bCol)
    {
		int[][] r = new int[n][n];
		if(c.length == aRow && c[0].length == bCol)
            return c;
        for(int i = 0; i < aRow; i++)
        {
            for(int j = 0; j < bCol; j++)
                r[i][j] = c[i][j];
        }
        return r;
    }
	//prints the matrix
	public static void printMatrix(int[][] c)
	{
	    for (int i = 0; i < n; i++) 
	    {
	    	for (int j = 0; j < n; j++) 
	    		System.out.print(c[i][j] + "\t");
			
			System.out.println();
	    }
	}

	public static void split(int[][] x, int[][] xDiv, int i, int j)
	{
		for (int k = 0; k < xDiv.length; k++)
		{
			for (int l = 0; l < xDiv.length; l++)
				xDiv[k][l] = x[k + i][l + j];
		}
	}

	public static int[][] add(int[][] a,int[][] b)
	{
		int[][] sum = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
				sum[i][j] = a[i][j] + b[i][j];
		}
		return sum;
	}

	public static int[][] sub(int[][] a,int[][] b)
	{
		int[][] difference = new int[a.length][a[0].length];
		for(int i = 0; i < a.length; i++)
		{
			for(int j = 0; j < a[0].length; j++)
				difference[i][j] = a[i][j] - b[i][j];
		}
		return difference;
	}

	public static void join(int[][] x, int[][] xDiv, int i, int j)
	{
		for(int k = 0; k < xDiv.length; k++)
		{
			for(int l = 0; l < xDiv.length; l++)
				x[k + i][l + j] = xDiv[k][l];
		}
	}
}