# breadth_first_search.py
"""Volume 2: Breadth-First Search.
Brandon Marshall
Section 2
Oct 31, 2018
"""
from collections import deque
import networkx as nx
from matplotlib import pyplot as plt
# Problems 1-3
class Graph:
    """A graph object, stored as an adjacency dictionary. Each node in the
    graph is a key in the dictionary. The value of each key is a set of
    the corresponding node's neighbors.

    Attributes:
        d (dict): the adjacency dictionary of the graph.
    """
    def __init__(self, adjacency={}):
        """Store the adjacency dictionary as a class attribute"""
        self.d = dict(adjacency)

    def __str__(self):
        """String representation: a view of the adjacency dictionary."""
        return str(self.d)

    # Problem 1
    def add_node(self, n):
        """Add n to the graph (with no initial edges) if it is not already
        present.

        Parameters:
            n: the label for the new node.
        """
        #adds node with empty neighbors
        if n in self.d.keys():
            return
        else:
            self.d.update({n : set()})
            return 
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 1
    def add_edge(self, u, v):
        """Add an edge between node u and node v. Also add u and v to the graph
        if they are not already present.

        Parameters:
            u: a node label.
            v: a node label.
        """
        #checks is u or v already in graph, if not, adds them
        if u not in self.d.keys():
            self.d.update({u : set()})
        if v not in self.d.keys():
            self.d.update({v: set()})
        #adds u and v to each other neighbor list
        self.d[u].add(v)
        self.d[v].add(u)
        return
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 1
    def remove_node(self, n):
        """Remove n from the graph, including all edges adjacent to it.

        Parameters:
            n: the label for the node to remove.

        Raises:
            KeyError: if n is not in the graph.
        """
        #check if node in graph
        if n not in self.d.keys():
            raise KeyError("Node is not in the graph")
        #remove node from Key list (also removes it's neighbor list at same time)
        self.d.pop(n)
        #check if n in any neighbor lists, if so, remove it
        for i in self.d.keys():
            if n in self.d[i]:
                self.d[i].remove(n)
        return
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 1
    def remove_edge(self, u, v):
        """Remove the edge between nodes u and v.

        Parameters:
            u: a node label.
            v: a node label.

        Raises:
            KeyError: if u or v are not in the graph, or if there is no
                edge between u and v.
        """
        #check if u and v are nodes in graph
        if u not in self.d.keys():
            raise KeyError("Node not in graph")
        if v not in self.d.keys():
            raise KeyError("Node not in graph")
        #check if u and v are in each other's neighbors lists
        if u not in self.d[v] and v not in self.d[u]:
            raise KeyError("Edge does not exist")
        #remove u and v from each other's neighbor lists
        if u == v:
           self.d[v].remove(v)
        else:
            self.d[u].remove(v)
            self.d[v].remove(u)
        return
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 2
    def traverse(self, source):
        """Traverse the graph with a breadth-first search until all nodes
        have been visited. Return the list of nodes in the order that they
        were visited.

        Parameters:
            source: the node to start the search at.

        Returns:
            (list): the nodes in order of visitation.

        Raises:
            KeyError: if the source node is not in the graph.
        """
        #list of visited nodes in order of visitation
        V = []
        #deque(being treated as a stack) of nodes that need to be visited in order they were discovered
        G = deque()
        #set of nodes that have been visited or that are marked to be visited
        M = set()
        G.append(source)
        M.add(source)
        #mark node has been visited, then add its neighbors to the deque and set, continue until all nodes have been visited (meaning deque is empty)
        while len(G) != 0:
            a = G.popleft()
            V.append(a)
            for i in self.d[a]:
                if i not in M:
                    G.append(i)
                    M.add(i)
        return V
        raise NotImplementedError("Problem 2 Incomplete")

    # Problem 3
    def shortest_path(self, source, target):
        """Begin a BFS at the source node and proceed until the target is
        found. Return a list containing the nodes in the shortest path from
        the source to the target, including endoints.

        Parameters:
            source: the node to start the search at.
            target: the node to search for.

        Returns:
            A list of nodes along the shortest path from source to target,
                including the endpoints.

        Raises:
            KeyError: if the source or target nodes are not in the graph.
        """
        #check both source and target are in graph
        if source not in self.d.keys() or target not in self.d.keys():
            raise KeyError("Source or target is not in the graph")
        #list containing the order nodes are visited in
        V = []
        #Stack of nodes to be visited
        G = deque()
        #visited nodes
        M = set()
        #at each step, adds node with parent node in it's set, used to backtrace
        Shortest = {}
        #initialize
        G.append(source)
        M.add(source)
        searching = True
        
        #if same
        if source == target:
            return list(target)
        
        while searching == True:
            #visit node
            a = G.popleft()
            for i in self.d[a]:
                #add the unvisited neighbors
                if i not in M:
                    G.append(i)
                    Shortest.update({i : a})
                    M.add(i)
                    # go till found
                    if i == target:
                        searching = False
                        break
        a = target
        #add target node
        V.insert(0,a)
        searching = True
        #backtrace using Shortest
        while searching == True:
            V.append(Shortest[a])
            if Shortest[a] == source:
                break
            else:
                a = Shortest[a]
        #since nodes were added to visited list in reverse order, flip it to have it in correct order
        V.reverse()
        return V
        raise NotImplementedError("Problem 3 Incomplete")


# Problems 4-6
class MovieGraph:
    """Class for solving the Kevin Bacon problem with movie data from IMDb."""

    # Problem 4
    def __init__(self, filename="movie_data.txt", encoding_file = "utf8"):
        """Initialize a set for movie titles, a set for actor names, and an
        empty NetworkX Graph, and store them as attributes. Read the speficied
        file line by line, adding the title to the set of movies and the cast
        members to the set of actors. Add an edge to the graph between the
        movie and each cast member.

        Each line of the file represents one movie: the title is listed first,
        then the cast members, with entries separated by a '/' character.
        For example, the line for 'The Dark Knight (2008)' starts with

        The Dark Knight (2008)/Christian Bale/Heath Ledger/Aaron Eckhart/...

        Any '/' characters in movie titles have been replaced with the
        vertical pipe character | (for example, Frost|Nixon (2008)).
        """
        self.G = nx.Graph()
        self.actors = set()
        self.titles = set()
        with open(filename, 'r', encoding = encoding_file) as rfile:
            line = rfile.readlines()
        for i in range(0,len(line)):
            #guarantees no white lines
            line[i] = line[i].strip().split('/')
            #add the movie to the graph
            self.titles.add(line[i][0])
            self.G.add_node(line[i][0])
            for j in range(1,len(line[i])):
                self.actors.add(line[i][j])
                #since add edge adds nodes to, we only need to add the edge between the movie and the actor to both insert the actor and add the edge
                self.G.add_edge(line[i][0], line[i][j])
        return  
        raise NotImplementedError("Problem 4 Incomplete")

    # Problem 5
    def path_to_actor(self, source, target):
        """Compute the shortest path from source to target and the degrees of
        separation between source and target.

        Returns:
            (list): a shortest path from source to target, including endpoints.
            (int): the number of steps from source to target, excluding movies.
        """
        return nx.shortest_path(self.G, source, target), nx.shortest_path_length(self.G, source, target) // 2
        raise NotImplementedError("Problem 5 Incomplete")

    # Problem 6
    def average_number(self, target):
        """Calculate the shortest path lengths of every actor to the target
        (not including movies). Plot the distribution of path lengths and
        return the average path length.

        Returns:
            (float): the average path length from actor to target.
        """
        #returns dictionary of shortest paths from target to every node in graph
        path = nx.shortest_path_length(self.G, target)
        #remove paths to movies
        for i in self.titles:
            if i in path.keys():
                 path.pop(i)
        #put in list so we can compute number between actors
        myList = list(path.values())
        for i in range(0,len(myList)):
            #since actors have a movie between them //2 removes movies from the path length
            myList[i] = myList[i] // 2
        avg = sum(myList) / len(myList)
        plt.hist(myList, bins = [i - .5 for i in range(8)])
        myString = "Path lengths from " + target + " to every other actor"
        plt.title(myString, fontsize = 12)
        plt.xlabel("Path length", fontsize = 10)
        plt.ylabel("# of actors", fontsize = 10)
        plt.show()        
        return avg   
        raise NotImplementedError("Problem 6 Incomplete")

