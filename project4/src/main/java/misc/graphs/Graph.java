package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    private IDictionary<V, IList<E>> map;
    private IList<V> allV;
    private IList<E> allE;
    
    public Graph(IList<V> vertices, IList<E> edges) {
        this.map = new ChainedHashDictionary<V, IList<E>>();
        this.allV = new DoubleLinkedList<V>();
        this.allE = new DoubleLinkedList<E>();
        for (V vertice: vertices) {
            this.map.put(vertice, new DoubleLinkedList<E>());
            this.allV.add(vertice);
        }
        for (E edge: edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            V vertix1 = edge.getVertex1();
            V vertix2 = edge.getVertex2();
            if (!this.allV.contains(vertix1) | !this.allV.contains(vertix2)) {
                throw new IllegalArgumentException();
            }
            this.map.get(vertix1).add(edge);
            this.map.get(vertix2).add(edge);
            this.allE.add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.allV.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.allE.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IDisjointSet<V> result = new ArrayDisjointSet<V>();
        ISet<E> output = new ChainedHashSet<E>();
        for (V vertix: allV) {
            result.makeSet(vertix);
        }
        IList<E> sorted = Searcher.topKSort(this.numEdges(), this.allE);
        for (E edge: sorted) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            if (result.findSet(v1) != result.findSet(v2)) {
                result.union(v1, v2);
                output.add(edge);
            }
        }
        return output;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        IList<E> path = new DoubleLinkedList<E>();
        if ((!this.allV.contains(start)) | (!this.allV.contains(end))) {
            throw new IllegalArgumentException();
        }
        if (start.equals(end)) {
            return path;
        }
        
        IPriorityQueue<Vertice<V>> vertices = new ArrayHeap<Vertice<V>>();
        ISet<V> visited = new ChainedHashSet<V>();
        IDictionary<V, IList<E>> pathToPoint = new ChainedHashDictionary<V, IList<E>>();
        IDictionary<V, Double> costs = new ChainedHashDictionary<V, Double>();
        
        for (V vertice : this.allV) {
            if (vertice.equals(start)) {
                costs.put(vertice, 0.0);
            } else {
                costs.put(vertice, Double.POSITIVE_INFINITY);
            }
        }
        vertices.insert(new Vertice<V>(start, 0));
        pathToPoint.put(start, new DoubleLinkedList<E>());
        
        while (!vertices.isEmpty()) {
            Vertice<V> vNow = vertices.removeMin();
            V pointNow = vNow.givePoint();
            
            if (!visited.contains(pointNow)) {
                visited.add(pointNow);
                double costNow = vNow.giveCost();
                IList<E> pathNow = pathToPoint.get(pointNow);
                IList<E> edges = this.map.get(vNow.givePoint());
                
                for (E edge : edges) {
                    V dest;
                    if (edge.getVertex1().equals(vNow.givePoint())) {
                        dest = edge.getVertex2();
                    } else {
                        dest = edge.getVertex1();
                    }
                    if (!visited.contains(dest)) {
                        double destCost = costNow + edge.getWeight();
                        IList<E> destPath = new DoubleLinkedList<E>();
                        for (E p : pathNow) {
                            destPath.add(p);
                        }
                        destPath.add(edge);
                        if (costs.containsKey(dest)) {
                            double destOldCost = costs.get(dest);
                            if (destOldCost > destCost) {
                                costs.put(dest, destCost);
                                pathToPoint.put(dest, destPath);
                                vertices.insert(new Vertice<V>(dest, destCost));
                            }
                        } else {
                            costs.put(dest, destCost);
                            pathToPoint.put(dest, destPath);
                            vertices.insert(new Vertice<V>(dest, destCost));
                        }
                    }
                }
            }
        }
        
        if (!pathToPoint.containsKey(end)) {
            throw new NoPathExistsException();
        }
        IList<E> output = pathToPoint.get(end);
        return output;
        
    }
    
    private class Vertice<V1> implements Comparable<Vertice<V1>> {
        private double cost;
        private V1 point;
        
        public Vertice(V1 point, double cost) {
            this.cost = cost;
            this.point = point;
        }
       
        public double giveCost() {
            return this.cost;
        }

        public V1 givePoint() {
            return this.point;
        }
        
        public int compareTo(Vertice<V1> other) {
            double ans = this.giveCost() - other.giveCost();
            if (ans < 0) {
                return -1;
            } else if (ans > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}


