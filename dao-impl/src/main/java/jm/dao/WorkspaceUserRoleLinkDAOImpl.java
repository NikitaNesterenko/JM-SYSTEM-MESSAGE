package jm.dao;


import jm.model.WorkspaceUserRoleLink;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;


import java.util.Collection;

@Repository
public class WorkspaceUserRoleLinkDAOImpl extends AbstractDao<WorkspaceUserRoleLink> {
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(Long id) {
       return (Collection<? extends GrantedAuthority>)entityManager.createQuery("from WorkspaceUserRoleLink where user_id  = :id")
               .setParameter("id", id).getResultList();
    }
}
