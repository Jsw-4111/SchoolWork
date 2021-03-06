#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>

void crossProduct(double **Mat, double *vec, int rownum, int vectorLength)
{
    double temp[rownum];
    printf("The results of the cross product are: ");
    for(int row = 0; row < rownum; row++)
    {
        double sum = 0;
        for(int element = 0; element < vectorLength; element++)
        {
            sum += Mat[row][element] * vec[element];
        }
        printf("%f ", sum);
    }
}

double *populateMatrix(int colnum, char buffer[])
{
    double *temp = (double*)calloc(colnum, sizeof(double));
    char *ptr;
    temp[0] = strtol(buffer, &ptr, 10);
    for(int i = 1; i < colnum; i++)
    {
        // sscanf(buffer, "%d", &temp[i]);
        temp[i] = strtol(ptr, &ptr, 10);
    }
    return temp;
}

double *populateVector(int vectorLength, char buffer[])
{
    double *temp = (double*)malloc(vectorLength*sizeof(double));
    char *ptr;
    temp[0] = strtol(buffer, &ptr, 10);
    for(int i = 1; i < vectorLength; i++)
    {
        // sscanf(buffer, "%d", &temp[i]);
        temp[i] = strtol(ptr, &ptr, 10);
    }
    return temp;
}

int main (int argc, char **argv)
{
    FILE *fRead;
    char buffer[255];
    fRead = fopen("mv.txt", "r");
    if(fRead == NULL)
    {
        printf("Error opening file: mv.txt\n");
        return 1;
    }
    struct timeval start, elapsed;
    gettimeofday(&start, NULL);
    int rownum, colnum;
    if (fgets(buffer, 256, fRead) != NULL)
    {
        sscanf(buffer, "%d %d", &rownum, &colnum);
    }
    int i = 0;
    double *Mat[rownum];
    while(i < rownum && fgets(buffer, 256, fRead) != NULL)
    {
        Mat[i] = populateMatrix(colnum, buffer);
        i++;
    }
    int vectorLength;
    if (fgets(buffer, 256, fRead) != NULL)
    {
        sscanf(buffer, "%d", &vectorLength);
    }
    double *vec;
    while(fgets(buffer, 256, fRead) != NULL)
    {
        vec = populateVector(vectorLength, buffer);
    }

    if(rownum == vectorLength)
        crossProduct(Mat, vec, rownum, vectorLength);
    else
    {
        printf("Matrix and Vector cannot be crossed together, please try different values.");
        return 0;
    }
    gettimeofday(&elapsed, NULL);
    printf("\nTotal time for computation: %f", (elapsed.tv_usec - start.tv_usec)/1000.0);
}