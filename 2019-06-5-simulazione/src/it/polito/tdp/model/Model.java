package it.polito.tdp.model;

import java.time.Year;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<Distretto, DefaultWeightedEdge> grafo;
		
	public Model() {
		grafo = new SimpleWeightedGraph<Distretto, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	public List<Year> getAnni() {
		EventsDao dao = new EventsDao();
		return dao.listAllYears();
	}
	
	public String getVicini(Year anno) {
		String risultato = "";
		EventsDao dao = new EventsDao();
		List<Distretto> distretti = dao.listDistretti(anno);
		for(Distretto d : distretti) {
			if(!grafo.containsVertex(d)) {
				grafo.addVertex(d);				
			}
		}
		
	// oppure	Graphs.addAllVertices(grafo, distretti);
		
		for(Distretto d1 : grafo.vertexSet()) {
			for(Distretto d2 : grafo.vertexSet()) {
				if(!d1.equals(d2)) {
					DefaultWeightedEdge edge = grafo.getEdge(d1, d2);
					if(edge==null) {
//						grafo.addEdge(d1, d2);
//						double distanza = LatLngTool.distance(d1.getLatlng(), d2.getLatlng(), LengthUnit.KILOMETER);
//						grafo.setEdgeWeight(edge, distanza);
						
						//oppure
						
						Graphs.addEdgeWithVertices(grafo, d1, d2, LatLngTool.distance(d1.getLatlng(), d2.getLatlng(), LengthUnit.KILOMETER));
					}
				}
			}
		}
		
		for(Distretto d : grafo.vertexSet()) {
			List<Distretto> vicini = Graphs.neighborListOf(grafo, d);
			Collections.sort(vicini, new Comparator<Distretto>() {

				@Override
				public int compare(Distretto o1, Distretto o2) {
					DefaultWeightedEdge e1 = grafo.getEdge(o1, d);
					double peso1 = grafo.getEdgeWeight(e1);
					DefaultWeightedEdge e2 = grafo.getEdge(o2, d);
					double peso2 = grafo.getEdgeWeight(e2);
					return (int) (peso1-peso2);
				}
			});
			
			for(Distretto dis : vicini) {
				DefaultWeightedEdge edge = grafo.getEdge(d, dis);
				risultato+= dis.getId()+" "+grafo.getEdgeWeight(edge)+"\n";
			}
		}
		
		return risultato;
	}
	
}
