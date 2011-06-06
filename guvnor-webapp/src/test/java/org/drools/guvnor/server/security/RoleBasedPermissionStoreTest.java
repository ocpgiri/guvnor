/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.security;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.RulesRepository;
import org.junit.Test;

public class RoleBasedPermissionStoreTest extends GuvnorTestBase {

    private RoleBasedPermissionStore getStore() throws Exception {
        RoleBasedPermissionStore store = new RoleBasedPermissionStore();

        ServiceImplementation impl = getServiceImplementation();
        RulesRepository repo = impl.getRulesRepository();
        
        store.repository = repo;
        
        return store;
    }

    @Test
    public void testGetRoleBasedPermissionsByUserName() throws Exception {
        RoleBasedPermissionStore store = getStore();

        store.addRoleBasedPermissionForTesting( "jervis",
                                                new RoleBasedPermission( "jervis",
                                                                         RoleType.PACKAGE_ADMIN.getName(),
                                                                         "package1Name",
                                                                         null ) );
        store.addRoleBasedPermissionForTesting( "jervis",
                                                new RoleBasedPermission( "jervis",
                                                                         RoleType.PACKAGE_READONLY.getName(),
                                                                         "package2Name",
                                                                         null ) );
        store.addRoleBasedPermissionForTesting( "jervis",
                                                new RoleBasedPermission( "jervis",
                                                                         RoleType.PACKAGE_READONLY.getName(),
                                                                         "package3Name",
                                                                         null ) );
        store.addRoleBasedPermissionForTesting( "jervis",
                                                new RoleBasedPermission( "jervis",
                                                                         RoleType.ANALYST.getName(),
                                                                         null,
                                                                         "category1" ) );
        store.addRoleBasedPermissionForTesting( "john",
                                                new RoleBasedPermission( "john",
                                                                         RoleType.ANALYST.getName(),
                                                                         null,
                                                                         "category2" ) );
        store.addRoleBasedPermissionForTesting( "johnson",
                                                new RoleBasedPermission( "johnson",
                                                                         RoleType.ADMIN.getName(),
                                                                         null,
                                                                         null ) );

        List<RoleBasedPermission> perms = store.getRoleBasedPermissionsByUserName( "jervis" );
        assertTrue( perms.size() == 4 );
        List<RoleBasedPermission> expectedPerms = new ArrayList<RoleBasedPermission>();
        expectedPerms.add( new RoleBasedPermission( "jervis",
                                                    RoleType.PACKAGE_ADMIN.getName(),
                                                    "package1Name",
                                                    null ) );
        expectedPerms.add( new RoleBasedPermission( "jervis",
                                                    RoleType.PACKAGE_READONLY.getName(),
                                                    "package2Name",
                                                    null ) );
        expectedPerms.add( new RoleBasedPermission( "jervis",
                                                    RoleType.PACKAGE_READONLY.getName(),
                                                    "package3Name",
                                                    null ) );
        expectedPerms.add( new RoleBasedPermission( "jervis",
                                                    RoleType.ANALYST.getName(),
                                                    null,
                                                    "category1" ) );
        for ( RoleBasedPermission perm : perms ) {
            for ( RoleBasedPermission expectedPerm : expectedPerms ) {
                if ( perm.getPackageName() != null && perm.getPackageName().equals( expectedPerm.getPackageName() ) && perm.getRole().equals( expectedPerm.getRole() ) ) {
                    expectedPerms.remove( expectedPerm );
                    break;
                } else if ( perm.getCategoryPath() != null && perm.getCategoryPath().equals( expectedPerm.getCategoryPath() ) && perm.getRole().equals( expectedPerm.getRole() ) ) {
                    expectedPerms.remove( expectedPerm );
                    break;
                }
            }
        }
        assertTrue( expectedPerms.size() == 0 );

        perms = store.getRoleBasedPermissionsByUserName( "john" );
        assertTrue( perms.size() == 1 );
        assertTrue( perms.get( 0 ).getRole().equals( RoleType.ANALYST.getName() ) );
        assertTrue( perms.get( 0 ).getUserName().equals( "john" ) );

        perms = store.getRoleBasedPermissionsByUserName( "johnson" );
        assertTrue( perms.size() == 1 );
        assertTrue( perms.get( 0 ).getRole().equals( RoleType.ADMIN.getName() ) );
        assertTrue( perms.get( 0 ).getUserName().equals( "johnson" ) );
    }

}
