#include<stdio.h>
int func1(int l[], int len) {
	int sum = 0, i = 0;
	while(i < len){
		sum = sum + l[i];
		i = i + 1;
	}
	return sum;	
} 

void func2(int l[][2]) {
	{
	}
	;
}

void func3(int l1[], int l2[], int l3[]) {
	return ;
}

void func4(int l1[][5], int l2[][5], int l3[][5]) {
	return ;
}

int main(){
	int list1[5] = {1, 2, 3, 4, 5};
	int list2[3] = {list1[2]+list1[1], list1[1]+list1[0], list1[0]+list1[2]};
	int list3[5] = {list1[0], list1[2], list1[4], list1[1], list1[3]};
	//int list2[3] = {5, 3, 4}, list3[5] = {1, 3, 5, 2, 4};
	int list4[3][2] = {{1, 2}, {3, 4}, {5, 6}};
	printf("20373420\n");
	printf("sum of list1 is %d, sum of list2 is %d\n", func1(list1, 5), func1(list2, 3));
	printf("sum of the first line of list4 is %d\n", func1(list4[0], 2));
	func3(list1, list2, list3);
	func2(list4);
	int a = list3[2];
	int b = (list3[0] + list3[4]) * list3[1] - list3[3];
	printf("a = %d, b = %d", a, b);
	return 0;
}

