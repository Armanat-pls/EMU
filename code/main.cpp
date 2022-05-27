#include "Tclass.h"
#include <Windows.h>
using std::cout;
using std::cin;

int main()
{
	SetConsoleCP(1251);
	SetConsoleOutputCP(1251);
	CPU A;
	A.work();
	cout<<"\nОтключение...\n";
	Sleep(1000);
	system("pause");
	return 0;
}

