/*
 *  Licensed to GIScience Research Group, Heidelberg University (GIScience)
 *
 *   http://www.giscience.uni-hd.de
 *   http://www.heigit.org
 *
 *  under one or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information regarding copyright
 *  ownership. The GIScience licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package heigit.ors.routing.graphhopper.extensions.reader.borders;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CountryBordersHierarchyTest {
    private CountryBordersHierarchy cbh;

    GeometryFactory gf = new GeometryFactory();

    CountryBordersPolygon cbp1, cbp2, cbp3;
    CountryBordersHierarchy cbh1, cbh2;
    Coordinate[] country1Geom = new Coordinate[] {
            new Coordinate(0,0),
            new Coordinate(0,1),
            new Coordinate(1,1),
            new Coordinate(1,0),
            new Coordinate(0,0)
    };
    Coordinate[] country2Geom = new Coordinate[] {
            new Coordinate(0.1,0.1),
            new Coordinate(-1,0.1),
            new Coordinate(-1,-1),
            new Coordinate(0.1,-1),
            new Coordinate(0.1,0.1)
    };
    Coordinate[] country3Geom = new Coordinate[] {
            new Coordinate(5,5),
            new Coordinate(10,5),
            new Coordinate(10,10),
            new Coordinate(5,10),
            new Coordinate(5,5)
    };

    public CountryBordersHierarchyTest() {
        try {
            cbp1 = new CountryBordersPolygon("name1", gf.createPolygon(country1Geom),-1);
            cbp2 = new CountryBordersPolygon("name2", gf.createPolygon(country2Geom),-1);
            cbp3 = new CountryBordersPolygon("name3", gf.createPolygon(country3Geom),-1);

            cbh1 = new CountryBordersHierarchy(1);
            cbh1.add(cbp1);
            cbh1.add(cbp2);

            cbh2 = new CountryBordersHierarchy(2);
            cbh2.add(cbp3);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Test that the hierarchies rturn correct bounding boxes that contain all polygon objects within them.
     */
    @Test
    public void GetBBoxTest() {
        double[] bbox = cbh1.getBBox();
        assertEquals(-1.0, bbox[0], 0.0);
        assertEquals(1.0, bbox[1], 0.0);
        assertEquals(-1.0, bbox[2], 0.0);
        assertEquals(1.0, bbox[3], 0.0);

        bbox = cbh2.getBBox();
        assertEquals(5.0, bbox[0], 0.0);
        assertEquals(10.0, bbox[1], 0.0);
        assertEquals(5.0, bbox[2], 0.0);
        assertEquals(10.0, bbox[3], 0.0);
    }

    /**
     * Test that hierarchies determine that a coordinate is within the bounding box containing all polygon objects
     */
    @Test
    public void InBBoxTest() {
        assertTrue(cbh1.inBbox(new Coordinate(0.5, 0.5)));
        assertTrue(cbh1.inBbox(new Coordinate(-0.5, 0.5)));
        assertFalse(cbh1.inBbox(new Coordinate(7.5, 7.5)));
        assertFalse(cbh1.inBbox(new Coordinate(100, 100)));
    }

    /**
     * Test that correct country objects are returned from the hierarchies based on a coordinate given.
     */
    @Test
    public void GetCountryTest() {
        ArrayList<CountryBordersPolygon> containing = cbh1.getContainingPolygons(new Coordinate(0.9, 0.9));
        assertEquals(1, containing.size());
        assertEquals("name1", containing.get(0).getName());

        containing = cbh1.getContainingPolygons(new Coordinate(0.0, 0.0));
        assertEquals(2, containing.size());

        containing = cbh1.getContainingPolygons(new Coordinate(10, 10));
        assertEquals(0, containing.size());
    }
}
