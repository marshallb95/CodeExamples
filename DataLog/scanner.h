#pragma once
#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <cctype>
#include "tokenizer.h"
#include <map>
#include <set>
using namespace std;

class Scanner {
    private:
        string filename;
        vector<string> scTokens;
        int lineNumber;
        int altLineNum;
        vector<int> tokenMap;
        vector<Token> completeTokens;
	friend class Parser;
    public:
        Scanner(string filename);
        void scanInfile();
        string tokenLabeler(string sToken);
        int count(string str, char ch);
        void makeTokens();
        void printTokens();
	vector<Token> getCompleteTokens() const;
};

