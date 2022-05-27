#include "Tclass.h"
#include "CMS.h"
#include <Windows.h>
#include <cmath>
using std::cout;
using std::cin;
using std::ifstream;
using std::ofstream;

// перевод двоичной в десятичную
int bin_to_int(bitset<CELL> data) 
{
	bitset<CELL-1> imp;
	for (int i=0; i<CELL-1;i++)
			imp[i]=data[i];
	if(data[CELL-1]==0)
		return imp.to_ulong();
	else
	{   //получение дополнительного кода
		imp.flip();
		return -imp.to_ulong()-1;
	}	
}

// перевод десятичной в двоичную
bitset<CELL> int_to_bit(int data) 
{
	bitset<CELL> imp;
	if (data < 0) //ситуация отрицательности
		imp[CELL-1] = 1;
	for (int i = CELL-2; i>=0; --i)
		imp[i]=(int)((data>>i)&1);
	return imp;
}

// склеивание двух int в двоичный код
bitset<CELL> make_one(int com, int addr) 
{
	bitset<CELL> imp; // переменная для результата
	bitset<CELL> B_com = int_to_bit(com); //двоичный код команды
	bitset<CELL> B_addr = int_to_bit(addr); //двоичный код адреса ячейки
	for (int i = 0; i < BMEM;i++) //склейка
		imp[i] = B_addr[i];
	for (int i = BMEM; i < CELL; i++)
		imp[i] = B_com[i-BMEM];
	return imp;
}

//Конструкторы
BRAIN::BRAIN()
{
	RO=0;
}

MEMORY::MEMORY()
{
	for (int i = 0; i < MEM; i++)
		for (int j = 0; j < CELL; j++)
			TABLE[i][j]=0;
		
}

CONTROL::CONTROL()
{
	CANT = 0;
	for (int i = 0;i<CELL ;i++)
		RC[i] = 0;
}

void BRAIN::write_RO(bitset<CELL> data)
{
	RO=data;
}
bitset<CELL> BRAIN::get_RO()
{
	return RO;
}

void BRAIN::showRO()
{
	cout<<"[RO]: ";
	for (int j = 0; j < CELL; j++)
	{
		cout << RO[CELL-1-j];
		if (j % 4 == 3)			
			cout <<" ";			
	}
	cout <<"\n";
}

void MEMORY::zero()
{
	for (int i = 0; i < MEM; i++)
		for (int j = 0; j < CELL; j++)
			TABLE[i][j]=0;
}

void MEMORY::write_cell(short addr, bitset<CELL> data)
{
	TABLE[addr]=data;
}

bitset<CELL> MEMORY::get_cell(short addr)
{
	return TABLE[addr];
}

//Заполнение RAM из файла
void MEMORY::fill_ALL()
{
	ifstream fin("data\\memory.txt");
		if (!fin) cout << "File failure\n";
//	bool tmp=0;
	for (int i = 0; i < MEM; i++)
		fin>>TABLE[i];	//чистый ввод	
	fin.close();
}
//Вывод RAM в файл
void MEMORY::dump_ALL()
{
	ofstream fout("data\\memoryDUMP_bin.txt");
		if (!fout) cout << "File failure\n";
	for (int i = 0; i < MEM; i++)
		fout<<TABLE[i]<<"\n";	//чистый вывод
	fout.close();
}

//Вывод на экран RAM
void MEMORY::show()
{
	cout<<"\t\t\t============ВСЯ ПАМЯТЬ============\n";
	for (int i = 0; i < MEM; i++)
	{
		if ((i % 20 == 1) && (i > 1))
			system("pause");
		if (i < 10)		
			cout<<"[00"<<i<<"]: ";
		else if (i < 100)
			cout<<"[0"<<i<<"]: ";
		else if (i < 1000)
			cout<<"["<<i<<"]: ";
		for (int j = 0; j < CELL; j++)
		{
			cout << TABLE[i][CELL-1-j];
			if (j % 4 == 3)			
				cout <<" ";				
		}
		cout <<"\n";
	}
}

void MEMORY::show_cell(short addr)
{
	cout<<"["<<addr<<"]: ";
	for (int j = 0; j < CELL; j++)
	{
		cout << TABLE[addr][CELL-1-j];
		if (j % 4 == 3)			
			cout <<" ";
	}
	cout <<"\n";
}

//деление команды на наборы 
void CONTROL::cut_com(bitset<CELL-BMEM> &C, bitset<BMEM> &A)
{
	//0 - BMEM
	for (int i = 0; i < BMEM; i++)
		A[i] = RC[i];
	//BMEM - CELL 
	for (int i = BMEM; i < CELL; i++)
		C[i-BMEM] = RC[i];
}

void CPU::work()
{	
	cout<<"================================================================\n\n";
	cout<<"\tЭмулятор ЭВМ "<<VER<<"\n\n";
	cout<<"Разрядность - "<<CELL<<" бит\n";
	cout<<"Разрядность памяти - "<<BMEM<<" бит -> "<<MEM<<" ячеек\n\n";
	cout<<"================================================================\n\n";
	short menu = 0; //переменная для управления меню
	short menu2 = 0; // переменная для выбора бинарного и десятичного режима при вводе ячейки
	short tmp = 0; //переменная для нового значения счётчика при смене или адреса ячейки просмотра
	do
	{
		cout<<"\nТекущая ячейка ["<<UU.CANT<<"]\n";
		RAM.show_cell(UU.CANT);
		cout<<"\n";
		cout<<"================================================================\n";
		cout<<"\t\t ВЫБЕРИТЕ ДЕЙСТВИЕ\n\n";
		cout<<"\t01 - Выполнить ячейку\n";
		cout<<"\t02 - Установить счётчк команд\n";
		cout<<"\t03 - Записать в произвольную ячейку\n";
		cout<<"\t04 - Просмотреть произвольную ячейку\n";
		cout<<"\t05 - Заполнить память из файла\n";
		cout<<"\t06 - Вывести всю память\n";
		cout<<"\t07 - Вывести регистр АЛУ\n";
		cout<<"\t08 - Записать память в файл\n";
		cout<<"\t09 - Автоматическая работа с этой точки\n";
		cout<<"\t10 - Обнулить память\n";
		cout<<"\t00 - EXIT\n\n";
		cout<<"================================================================\n";
		cout<<"Номер действия : ";
		cin>>menu;
		switch (menu)
		{
		case 1:	// пошаговое выполнение
			cout<<"\nПошаговое выполнение\n";
			UU.RC=RAM.get_cell(UU.CANT);
			if(compute() == 666)
				cout<<"Встречена команда завершения\n";
			Sleep(250);
			break;
		case 2:	// выставить счётчик команд
			cout<<"\nУстановка счётчика команд\n";
			cout<<"Введите адрес ячейки памяти\n";
			cin>>tmp;
			UU.CANT=tmp; 
			break;
		case 3:	//три способа записи ячейки
			cout<<"\nВвод данных в ячейку\n\n";
			cout<<"0 - Ввести ячейку целиком в бинарном формате;\n";
			cout<<"1 - Ввести число в десятичном формате;\n";
			cout<<"2 - Ввести команду и операнд в десятичном формате\n";
			cin>>menu2;
			if (menu2 == 0)
			{
				bitset<CELL> tmp; // переменная для данных ячейки
				short ad = 0; // переменная для выбора адреса ячейки
				cout<<"Введите адрес ячейки (текущая ячейка: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"Введите 32 бита без пробелов\n";
				cin>>tmp;
				RAM.write_cell(ad,tmp);
			}
			else if (menu2 == 1)
			{
				int tmp = 0; 	// переменная для данных ячейки
				short ad = 0; // переменная для выбора адреса ячейки
				cout<<"Введите адрес ячейки (текущая ячейка: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"Введите число\n";
				cin>>tmp;
				RAM.write_cell(ad,int_to_bit(tmp));				
			}
			else if (menu2 == 2)
			{
				int tmp = 0, oper = 0; 	// переменные для команды и операнда
				short ad = 0; // переменная для выбора адреса ячейки
				cout<<"Введите адрес ячейки (текущая ячейка: "<<UU.CANT<<")\n";
				cin>>ad;
				cout<<"Введите номер команды и адрес операнда\n";
				cin>>tmp>>oper;
				RAM.write_cell(ad, make_one(tmp,oper));
			}
			break;
		case 4:	// взглянуть на ячейку
			cout<<"\nПросмотр произвольной ячейки\n";
			cout<<"Введите адрес ячейки памяти для просмотра\n";
			cin>>tmp;
			RAM.show_cell(tmp);
			cout<<"\n";
			system("pause");
			break;
		case 5: // заполнение памяти из файла
			cout<<"\nЗаполнение памяти из файла\n";
			RAM.fill_ALL();
			cout<<"\nПамять считана из файла\n";
			Sleep(1000);
			break;
		case 6:	// полный вывод
			cout<<"\nПолный вывод\n";
			RAM.show();
			Sleep(1000);
			break;
		case 7:	// Вывод регистра АЛУ
			cout<<"\nВывод регистра АЛУ\n";
			ALU.showRO();
			Sleep(1000);
			break;
		case 8:	// дамп в файл
			cout<<"\nЗапись памяти в файл\n";
			RAM.dump_ALL();
			cout<<"\nПамять выгружена в файл\n";
			Sleep(1000);
			break;
		case 9: // автоматический режим до конца
			cout<<"\nПоследовательное выполнение\n";
			while(1)
			{
				UU.RC=RAM.get_cell(UU.CANT);
				if(compute() == 666)
				{
					cout<<"Встречена команда завершения\n";
					Sleep(1000);
					break;
				}
				if (UU.CANT == MEM)
				{
					UU.CANT--;
					break;
				}
			}	
			break;
		case 10:
			cout<<"\nОчистка памяти\n";
			RAM.zero();
			cout<<"Память обнулена\n";
			Sleep(1000);
			break;
		default:	//завершение работы
			menu = 666;
			break;
		}
	} while (menu != 666);
	cout<<"Работа завершена \n";
}

short CPU::compute()
{	
	bitset<CELL-BMEM> C;	//набор для команды
	bitset<BMEM> A;		//набор для адреса
	UU.cut_com(C, A);
	if (C.to_ulong() == CMS::STOP)
		return 666;
	int op1, op2, res;
	op1 = bin_to_int(ALU.get_RO());
	op2 = bin_to_int(RAM.get_cell(A.to_ulong()));
	switch (C.to_ulong())
	{
	case CMS::JUMP:
		UU.CANT=A.to_ulong();
		return 101;//Выход из выполнения без дополнительного повышения счётчика команд
	case CMS::LOAD:
		ALU.write_RO(RAM.get_cell(A.to_ulong()));//запись значения ячейки А в аккумулятор
		break;
	case CMS::SAVE:
		RAM.write_cell(A.to_ulong(),ALU.get_RO());//запись значения аккумуляторa в ячейку A
		break;
	case CMS::AND:
		res = (op1 && op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::OR:
		res = (op1 || op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::NOT:
		res = (!op2);
		ALU.write_RO(int_to_bit(res));
	break;
	case CMS::PLUS:
		res = (op1 + op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::MINUS:
		res = (op1 - op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::MULT:
		res = (op1 * op2);
		ALU.write_RO(int_to_bit(res));
		break;
	case CMS::DIVIS:
		if (op2 == 0)
		{
			res = 0;
			ALU.write_RO(int_to_bit(res));
			break;
		}	
		res = (op1 / op2);
		ALU.write_RO(int_to_bit(res));
		break;
	}
	UU.CANT++;//повышение счётчика команд после выполнения
	return 0;
}
