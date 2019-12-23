#pragma once
#include <iostream>
#include <vector>
#include <fstream>
#include <string>

using namespace std;

class Token {
    private:
        int lineNumber;
        string strToken;
        string tokenLabel;
        
    public:
        Token(string label, string token, int lineNum);
        void printToken();
	string getLabel() const;
	int getLineNumber() const;
	string getStrToken() const;
};
