package com.spawpaw.shortestpath;

import java.io.Serializable;

/**
 * Created by CDFAE1CC on 2016.12.13.
 */
class Edge  implements Serializable {
    Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = (float) Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2));
    }

    Vertex v1;
    Vertex v2;

    float weight;
}
