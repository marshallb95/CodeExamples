#include "Node.h"
#include <iostream>
#include <set>
#include <string>
#include <map>
#include <stack>
#include <vector>
#include "SCC.h"
class Graph {
	private:
		map<int,Node> nodes;
	public:
		Graph();
		void addNode(Node a);
		void addEdge(int a, int b);
		void DFS(stack<int> &visitOrder, vector<int> &postorder, int nodeInd);
		void DFSOrdered(vector<int> order, stack<int> &visitOrder, SCC &scc, int nodeInd);
		vector<int> DFSTree(vector<int> order, bool backwards);
		vector<SCC> DFSTreeSCC(vector<int> order);
		set<int> getNodes(string name);
		Node getNode(int value);
		int getSize() const;
		string toString();

};	

