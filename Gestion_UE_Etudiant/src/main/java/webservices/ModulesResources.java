package webservices;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entities.Module;
import entities.UniteEnseignement;
import metiers.UniteEnseignementBusiness;

@Path("/modules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModulesResources {

    // La liste est gérée directement dans le web service
    private static List<Module> modules = new ArrayList<>();
    private UniteEnseignementBusiness uniteEnseignementBusiness = new UniteEnseignementBusiness();

    // Initialisation de la liste
    static {
        UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();
        modules.add(new Module("M101", "Algorithmique", 3, 30, Module.TypeModule.PROFESSIONNEL, ueBusiness.getUEByCode(1)));
        modules.add(new Module("M102", "Base de données", 2, 20, Module.TypeModule.PROFESSIONNEL, ueBusiness.getUEByCode(1)));
        modules.add(new Module("M201", "Communication", 1, 15, Module.TypeModule.TRANSVERSAL, ueBusiness.getUEByCode(2)));
    }

    // GET - Récupérer tous les modules
    @GET
    @Path("/all")
    public Response getAllModules() {
        try {
            return Response.ok(modules).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // POST - Ajouter un nouveau module
    @POST
    @Path("/ajouterModule")
    public Response addModule(Module module) {
        try {
            System.out.println("Tentative d'ajout du module: " + module.getMatricule());

            // Vérifier si le module existe déjà
            if (getModuleByMatriculeFromList(module.getMatricule()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Un module avec ce matricule existe déjà")
                        .build();
            }

            // Gérer l'UE si elle est spécifiée
            if (module.getUniteEnseignement() != null) {
                int code = module.getUniteEnseignement().getCode();
                UniteEnseignement ue = uniteEnseignementBusiness.getUEByCode(code);
                if (ue != null) {
                    module.setUniteEnseignement(ue);
                }
            }

            // Ajouter le module à la liste
            modules.add(module);

            System.out.println("Module ajouté avec succès: " + module.getMatricule());
            System.out.println("Nombre total de modules: " + modules.size());

            return Response.status(Response.Status.CREATED)
                    .entity(module)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur serveur: " + e.getMessage())
                    .build();
        }
    }

    // PUT - Mettre à jour un module
    @PUT
    @Path("/modifierModule/{matricule}")
    public Response updateModule(@PathParam("matricule") String matricule, Module updatedModule) {
        try {
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i).getMatricule().equals(matricule)) {
                    // Gérer l'UE si elle est spécifiée
                    if (updatedModule.getUniteEnseignement() != null) {
                        int code = updatedModule.getUniteEnseignement().getCode();
                        UniteEnseignement ue = uniteEnseignementBusiness.getUEByCode(code);
                        if (ue != null) {
                            updatedModule.setUniteEnseignement(ue);
                        }
                    }

                    modules.set(i, updatedModule);
                    return Response.ok(updatedModule).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Module non trouvé")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // DELETE - Supprimer un module
    @DELETE
    @Path("/deleteModule/{matricule}")
    public Response deleteModule(@PathParam("matricule") String matricule) {
        try {
            Iterator<Module> iterator = modules.iterator();
            while (iterator.hasNext()) {
                Module m = iterator.next();
                if (m.getMatricule().equals(matricule)) {
                    iterator.remove();
                    return Response.ok("Module supprimé avec succès").build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Module non trouvé")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // Méthode utilitaire pour rechercher un module par matricule
    private Module getModuleByMatriculeFromList(String matricule) {
        for (Module m : modules) {
            if (m.getMatricule().equals(matricule)) {
                return m;
            }
        }
        return null;
    }
}