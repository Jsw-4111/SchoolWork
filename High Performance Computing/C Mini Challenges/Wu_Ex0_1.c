#include <stdio.h>

int main(int argc, char **argv)
{
    char name[128];
    printf("Hello, what is your name?\n");
    fgets(name, 128, stdin);

    printf("Hello %s %s \n", name);

    printf("Give me another name to try out scanf with\n");

    char nm[128];
    scanf("%[^\n]%*c", nm);

    printf("Hello, %s", nm);
}