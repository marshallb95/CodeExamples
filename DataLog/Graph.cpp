#include "Graph.h"
#include <stack>

Graph::Graph() {
	return;
}

void Graph::addNode(Node a) {
	nodes[a.getValue()] = a;
	return;
}

void Graph::addEdge(int a, int b) {
	Node nodeA;
	Node nodeB;
	//get nodes
	nodeA = nodes[a];
	nodeB = nodes[b];
	//update node neighbors
	nodeA.addDependency(nodeB);
	//update graph
	nodes[a] = nodeA;
	return;
}

void Graph::DFS(stack<int> &visitOrder, vector<int> &postorder, int nodeInd) {
	//cout << "Node that is under consideration is " << nodeInd << endl;

	if(nodes[nodeInd].getVisited() == false) {
		//cout << "Node has not been visited" << endl;
		//visit and recursively search
		nodes[nodeInd].visit();
		//check to make sure node visited value is physically altered in the graph
		//cout << "visited value is" << endl;
		//cout << nodes[nodeInd].getVisited() << endl;
		//cout << "Value being push onto the stack is " << nodeInd << endl << endl;
		visitOrder.push(nodeInd);
		// for each of its neighbors, recursively search
		//cout << "Number of neighbors is " << nodes[nodeInd].getDependencies().size() << endl;
		for(auto t: nodes[nodeInd].getDependencies()) {
			//cout << "In neighbors: node under consideration is " << t << endl;
			//trouble arises with self loops, keep this in mind as the fix may cause problems with scc later
			if(t == nodeInd) {
				//cout << "Node has edge to itself, thus continuing" << endl;
				continue;
			}
			else {
				//cout << "Neighbor that will be visited is " << t << endl;
				DFS(visitOrder, postorder, t);
			}
		}
		//cout << "Node that is on top of the stack is " << visitOrder.top()  << endl;
		postorder.push_back(visitOrder.top());
		visitOrder.pop();
		if(visitOrder.size() == 0) {
			//cout << "Stack is empty" << endl;
		}
		else { 
			//cout << "Node that is now on top of the stack is " << visitOrder.top() << endl;
		}
		//cout << "Left DFS" << endl << endl;
		return;
	}
	else {
		//cout << "Node was already visited" << endl;
		return;
	}
}

void Graph::DFSOrdered(vector<int> order, stack<int> &visitOrder, SCC &scc, int nodeInd) {
	//cout << "Entered DFSOrdered " << endl << endl;
	//cout << "SCC at start is: " << endl << scc.toString() << endl;
	//cout << "Node under consideration is " << nodeInd << endl << endl;
	vector<int> orderedNeighbors;
	if(nodes[nodeInd].getVisited() == false) {
		//cout << "Node has not been visited" << endl;
		nodes[nodeInd].visit();
		//cout << "After visiting, node visited is " << nodes[nodeInd].getVisited() << endl << endl;
		//cout << "Node being added to the stack is " << nodeInd << endl;
		visitOrder.push(nodeInd);
		//sort neighbors
		if(nodes[nodeInd].getDependencies().size() == 1) {
			//cout << "Node only had one neighbor: ";
			for(auto t: nodes[nodeInd].getDependencies()) {
				//cout << t << endl;
				orderedNeighbors.push_back(t);
			}
		}
		else if(nodes[nodeInd].getDependencies().size() > 1) {
			//sort neighbors according to order
			for(unsigned int i=0; i < order.size(); ++i) {
				for(auto t: nodes[nodeInd].getDependencies()) {
					if(t == order[i]) {
						//cout << "In sort: neighbor being added is " << t << "vs " << order[i] << endl;
						orderedNeighbors.push_back(order[i]);
					}
				}
			}
		}

		/*cout << "After being sorted, neighbors are:";
		for(unsigned int i = 0; i < orderedNeighbors.size();++i) {
			cout << orderedNeighbors[i] << " ";
		}
		cout << endl;*/
		//after sorting recursively search neighbors
		for(unsigned int i = 0; i < orderedNeighbors.size(); ++i) {
			if(orderedNeighbors[i] == nodeInd) {
				//cout << "Node has loop to itself" << endl;
				scc.setLoop();
			}
			//recursively search
			//cout << "Neighbor that will be searched recursively is " << orderedNeighbors[i] << endl;
			DFSOrdered(order, visitOrder, scc, orderedNeighbors[i]);
		}
		scc.addNode(visitOrder.top());
		visitOrder.pop();
		//cout << "leaving DFSTOrdered" << endl;
		//cout << "SCC is" << scc.toString();
		return;
	}
	else {
		return;
	}
}

vector<SCC> Graph::DFSTreeSCC(vector<int> order) {
	vector<SCC> postorder;
	stack<int> visitOrder;
	for(unsigned int i = 0; i < order.size(); ++i) {
		SCC scc;
		DFSOrdered(order,visitOrder,scc, order[i]);
		//cout << "scc after DFSOrdered is: " << endl << scc.toString() << endl;
		postorder.push_back(scc);
	}
	
	return postorder;
}
	


vector<int> Graph::DFSTree(vector<int> order, bool backwards) {
	//cout << "Entered DFSTree" << endl;
	//cout << "backwards boolean is " << backwards << endl;
	//cout << "order vector is" << endl;
	/*for(unsigned int i = 0; i < order.size(); ++i) {
		cout << order[i] << " ";
	}
	cout << endl;*/
	stack<int> visitOrder;
	vector<int> postorder;
	//make sure all nodes are marked as unvisited
	for(unsigned int i = 0; i < nodes.size(); ++i) {
		nodes[i].initializeVisit();
	}
	//if reverse graph
	if(backwards == true) {
		for(unsigned int i = 0; i < order.size(); ++i) {
			DFS(visitOrder, postorder, order[i]);
		}
	}
	return postorder;
}

set<int> Graph::getNodes(string name) {
	set<int> nodeRules;
	Node node;
	for(unsigned int i = 0; i < nodes.size(); ++i) {
		node = nodes[i];
		if(node.getName() == name) {
			//cout << "Node name is " << name << " and index is " << i << endl;
			nodeRules.insert(node.getValue());
		}
	}
	return nodeRules;
}

Node Graph::getNode(int value) {
	return nodes[value];
}

int Graph::getSize() const{
	return nodes.size();
}

string Graph::toString() {
	string str = "";
	map<int,Node>::iterator it;
	for(it = nodes.begin(); it != nodes.end(); ++it) {
		str += it->second.toString();
	}
	return str;
}	
