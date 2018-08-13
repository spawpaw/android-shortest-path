package com.spawpaw.shortestpath;


import java.io.Serializable;

/**
 * Created by CDFAE1CC on 2016.12.13.
 */
class Vertex implements Serializable {
    boolean isOccupied = false;
    //点在屏幕上的位置
    float x;
    float y;
    //所连接的下一个点的链表
    public NeighbourVertex next = null;

    Vertex(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //向邻接链表中添加下一个点的信息
    public void addNext(int index, float weight) {
        NeighbourVertex neighbour = new NeighbourVertex(index, weight);
        neighbour.next = this.next;
        this.next = neighbour;
    }


    class NeighbourVertex implements Serializable {
        NeighbourVertex(int nextIndex, float weight) {
            this.nextIndex = nextIndex;
            this.weight = weight;
        }

        float weight;
        int nextIndex;
        NeighbourVertex next;
    }
}
