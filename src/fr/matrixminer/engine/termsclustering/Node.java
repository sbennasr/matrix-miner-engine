package fr.matrixminer.engine.termsclustering;


import java.util.ArrayList;
import java.util.List;


public class Node implements Comparable<Node> {

	final String name;
	

	public Node(final String argName) {
		name = argName;
	}

	public int compareTo(final Node argNode) {
		return argNode == this ? 0 : -1;
	}

	public String toString() {
		return  name ;
	}
	


	public boolean existe(List<Node> existingVertexs) {
		boolean existe = false;
		for (Node n :existingVertexs){
			if (n.equals(this)){
				existe= true;
				break;
				}
		}
		return existe;
	}

	public String getName() {
		return name;
	}
}