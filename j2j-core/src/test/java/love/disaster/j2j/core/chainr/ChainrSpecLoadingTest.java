/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.disaster.j2j.core.chainr;


import love.disaster.j2j.core.Chainr;
import love.disaster.j2j.core.chainr.instantiator.DefaultChainrInstantiator;
import love.disaster.j2j.core.chainr.spec.ChainrEntry;
import love.disaster.j2j.core.chainr.spec.ChainrSpec;
import love.disaster.j2j.core.exception.SpecException;
import love.disaster.j2j.utils.JsonUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class ChainrSpecLoadingTest {
    @DataProvider
    public Object[][] badFormatSpecs() throws IOException {
        return new Object[][] {
                {JsonUtils.classpathToObject( "/json/chainr/specloading/bad_spec_SpecTransform.json" )}
        };
    }

    @Test(dataProvider = "badFormatSpecs", expectedExceptions = SpecException.class )
    public void testBadSpecs( Object chainrSpecObj ) {
        ChainrSpec chainrSpec = new ChainrSpec( chainrSpecObj );
        ChainrEntry chainrEntry = chainrSpec.getChainrEntries().get( 0 );
        DefaultChainrInstantiator instantiator = new DefaultChainrInstantiator();

        // This should fail
        instantiator.hydrateTransform( chainrEntry );
    }

    @Test(dataProvider = "badFormatSpecs", expectedExceptions = SpecException.class )
    public void staticChainrMethod( Object chainrSpec ) {
        Chainr.fromSpec( chainrSpec ); // should fail when parsing spec
    }
}
