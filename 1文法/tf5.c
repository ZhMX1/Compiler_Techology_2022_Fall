#include<stdio.h>
int getint(){
	int n;
	scanf("%d",&n);
	return n;
}

void func() {
	int i = 0;
	while(1) {
		if(i == 0) {
			i = i + 1;
			continue;
		}
		i = 100;
		break;	
	} 
	return ;
}
void func1(int a, int b, int c) {
	int i;
	int j = 1, k = 1;
	const int i1 = 1, i2_ = 2, i_3 = 3, i4_I = 4;
	return;
}
int Fu1_nC() {
	int i = 0;
	if(!i) {
		return 1 + 2;
	}
	return 2 + 2;
}
int func2() {
	int i = 1, ret = 0;
	if(i != 0){
		ret = 222;
	}

	else {
		ret = 666;
	}
	return ret;
}
int func_3() {
	int i = 1, j = 2;
	
	return i + j; 
}
int func4(int x, int y) {
	return (2 * x + y / 3) / 5 % 10;
}

int main(){
	printf("20373420\n");
	printf("Fu1_nC(): %d\n", Fu1_nC());
	printf("func2(): %d\n", func2());
	printf("func_3(): %d\n", func_3());
	
	int a;//������� 
	a = getint();
	if(a >= 0) {
		printf("a is no less than 0!\n");
	}else{
		printf("a is no greater than 0!\n");
	}
	
	int b;
	b = getint();
	if(b > 0) {
		printf("b is greater than 0!\n");
	}
	if(b <= 0){
		printf("b is no greater than 0!\n");
	}
	
	int c;
	c = getint();
	if(c == 0) {
		printf("c is exactly zero!\n");
		c = 13;
	}
	
	int i = 1;
	while(1) {
		i = i * 2;
		if(i > 100){
			break;
		}
	}
	func1(a, b, c);
	printf("i break with a value of %d\n", i);
	
	printf("func4 result: %d", func4(b, c));

	return 0;
}

