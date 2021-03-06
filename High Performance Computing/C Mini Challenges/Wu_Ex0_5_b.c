//PLACE YOUR INCLUDE STATEMENTS HERE
#include <stdio.h>
#include <sys/types.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char **argv) {
  int i,j;
  int n = atoi(argv[1]);
  double sum;
  clock_t end, start;
  double *arr = malloc(n*n*sizeof(double));
  
  printf("n is: %d \n", n);
  // check if malloc() was successful
  if( arr == NULL ) {
	// malloc() failed so exit
	printf("ERROR: malloc(%lu) with n = %d failed. Exiting. \n", (n*n*sizeof(double)), n);
	return 1;
	}
	
  
// THIS FILLS THE MATRIX WITH NUMBERS
  for (i=0; i<n; i++)
    for (j=0; j<n; j++)
      arr[i*n+j] = (double) rand()/RAND_MAX;

  sum = 0;

// ROW MAJOR WORK
// YOU'LL NEED TO TIME IT
start = clock();
for (i = 0; i<n; i++) // iterate over rows 
  for (j = 0; j<n; j++) // iterate over columns 
    sum += arr[i*n + j];
end = clock();

// NOTE:  YOU'LL NEED TO PROVIDE MEANING TO end AND start
  printf("Row Major: sum = %lf and Clock Ticks are %ld\n",sum,end-start);

//ADD YOUR COLUMN MAJOR WORK
// YOU'LL NEED TO TIME IT
start = clock();
for (i = 0; i<n; i++) // iterate over rows 
  for (j = 0; j<n; j++) // iterate over columns 
    sum += arr[i + j*n];
end = clock();
  printf("Column Major: sum = %lf and Clock Ticks are %ld\n",sum,end-start);
  return 0;
}

