package pcd.threadVersion.model;

import pcd.sketch01.V2d;

public record P2d(double x, double y)  {

    public P2d sum(pcd.sketch01.V2d v){
        return new P2d(x+v.x(),y+v.y());
    }

    public pcd.sketch01.V2d sub(P2d v){
        return new V2d(x-v.x(),y-v.y());
    }
    
    public String toString(){
        return "P2d("+x+","+y+")";
    }

    public double x() {
    	return x;
    }

    public double y() {
    	return y;
    }
}

