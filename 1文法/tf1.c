#include<stdio.h>
int global_var = 0;
int func() {
	global_var = global_var + 1;
	return 1;
}
int main() {
	if (0 && func()){
		;
	}
	printf("%d\n",global_var); // Êä³ö 0
	if (1 || func()) {
		;
	}
	printf("%d", global_var); // Êä³ö 0
	return 0;
}

