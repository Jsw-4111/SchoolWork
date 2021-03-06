//PLACE YOUR INCLUDE STATEMENTS HERE
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char **argv) {
  int i,j;
  int n = 128;
  double sum;
  clock_t end, start;
  double arr[128][128];
  
// THIS FILLS THE MATRIX WITH NUMBERS
  for (i=0; i<n; i++)
    for (j=0; j<n; j++)
      arr[i][j] = (double) rand()/RAND_MAX;

  sum = 0;

// ROW MAJOR WORK
// YOU'LL NEED TO TIME IT
start = clock();
for (i = 0; i<n; i++) // iterate over rows 
  for (j = 0; j<n; j++) // iterate over columns 
    sum += arr[i][j];
end = clock();

// NOTE:  YOU'LL NEED TO PROVIDE MEANING TO end AND start
  printf("Row Major: sum = %lf and Clock Ticks are %ld\n",sum, end-start);

//ADD YOUR COLUMN MAJOR WORK
// YOU'LL NEED TO TIME IT
start = clock();
for (i = 0; i<n; i++) // iterate over rows 
  for (j = 0; j<n; j++) // iterate over columns 
    sum += arr[j][i];
end = clock();
  printf("Column Major: sum = %lf and Clock Ticks are %ld\n",sum,end-start);
  return 0;
}

