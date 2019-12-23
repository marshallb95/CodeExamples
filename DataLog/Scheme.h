#pragma once
#include <iostream>
#include <string>
#include <vector>
using namespace std;

class Scheme {
    private:
	string name;
	vector<string> attributes;	
    public:
	Scheme();
	void setName(string Name);
	void addAttribute(string attribute);
	vector<string> getAttributes();
	void changeAttribute(string newName, int index);
	void removeAttribute(int index);
	void setAttributes(vector<string> newAttributes);
};
