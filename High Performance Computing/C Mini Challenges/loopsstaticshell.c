//PLACE YOUR INCLUDE STATEMENTS HERE

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
for (i = 0; i<n; i++) // iterate over rows 
  for (j = 0; j<n; j++) // iterate over columns 
    sum += arr[i][j];

// NOTE:  YOU'LL NEED TO PROVIDE MEANING TO end AND start
  printf("Row Major: sum = %lf and Clock Ticks are %ld\n",sum,end-start);

//ADD YOUR COLUMN MAJOR WORK
// YOU'LL NEED TO TIME IT

  return 0;
}

