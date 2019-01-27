package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it! 
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> output = new ChainedHashDictionary<URI, ISet<URI>>();
        ISet<URI> webURIs = new ChainedHashSet<URI>();
        for (Webpage page : webpages) {
            webURIs.add(page.getUri());
        }
        for (Webpage page : webpages) {
            URI thisURI = page.getUri();
            IList<URI> links = page.getLinks();
            ISet<URI> thisLinks = new ChainedHashSet<URI>();
            for (URI link : links) {
                if (link != thisURI & webURIs.contains(link)) {
                    thisLinks.add(link);
                }
            }
            output.put(thisURI, thisLinks);
        }
        return output;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        IDictionary<URI, Double> output = new ChainedHashDictionary<URI, Double>();
        IDictionary<URI, Double> newRanks = new ChainedHashDictionary<URI, Double>();
        double size = (double) graph.size();
        for (KVPair<URI, ISet<URI>> pair : graph) {
            output.put(pair.getKey(), 1 / size);
            newRanks.put(pair.getKey(), 0.0);
        }
        
        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            double conv = 0.0;
            for (KVPair<URI, ISet<URI>> j : graph) {
                double outgoing = graph.get(j.getKey()).size();
                double update = 0.0;
                double oldRank = output.get(j.getKey());
                if (outgoing == 0.0) {
                    update = decay * oldRank / size;
                    for (KVPair<URI, Double> k : newRanks) {
                        newRanks.put(k.getKey(), k.getValue() + update);
                    }
                } else {
                    update = decay * oldRank / outgoing;
                    for (URI k : j.getValue()) {
                        newRanks.put(k, newRanks.get(k) + update);
                    }
                }
                newRanks.put(j.getKey(), newRanks.get(j.getKey()) + (1 - decay) / size);
                if (Math.abs(newRanks.get(j.getKey()) - output.get(j.getKey())) > conv) {
                    conv = Math.abs(newRanks.get(j.getKey()) - output.get(j.getKey()));
                }
            }
            
            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            for (KVPair<URI, Double> key : output) {
                output.put(key.getKey(), newRanks.get(key.getKey()));
                newRanks.put(key.getKey(), 0.0);
            }
            if (conv < epsilon) {
                return output;
            } 
        }
        return output;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return this.pageRanks.get(pageUri);
    }
}
