package webservices;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entities.UniteEnseignement;

@Path("/ues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UEResources {

    // La liste est gérée directement dans le web service
    private static List<UniteEnseignement> unitesEnseignement = new ArrayList<>();

    // Initialisation de la liste
    static {
        unitesEnseignement.add(new UniteEnseignement(1, "Informatique", "Mme Maroua Douiri", 6, 1));
        unitesEnseignement.add(new UniteEnseignement(2, "Mathématiques", "Mme Ines ElMejid", 5, 1));
        unitesEnseignement.add(new UniteEnseignement(3, "Physique", "Mme Sarra Abidi", 4, 2));
        unitesEnseignement.add(new UniteEnseignement(4, "Infographie", "Mme Oumeima Ibnelfkih", 3, 1));
        unitesEnseignement.add(new UniteEnseignement(5, "Chimie", "M. Mohamed Amine Chebbi", 4, 2));
    }

    // GET - Récupérer toutes les UEs
    @GET
    @Path("/all")
    public Response getAllUEs() {
        try {
            return Response.ok(unitesEnseignement).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // GET - Récupérer une UE par son code
    @GET
    @Path("/{code}")
    public Response getUEByCode(@PathParam("code") int code) {
        try {
            UniteEnseignement ue = getUEByCodeFromList(code);
            if (ue != null) {
                return Response.ok(ue).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("UE non trouvée")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // GET - Récupérer les UEs par domaine
    @GET
    @Path("/domaine/{domaine}")
    public Response getUEsByDomaine(@PathParam("domaine") String domaine) {
        try {
            List<UniteEnseignement> result = new ArrayList<>();
            for (UniteEnseignement ue : unitesEnseignement) {
                if (ue.getDomaine().equalsIgnoreCase(domaine)) {
                    result.add(ue);
                }
            }
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // GET - Récupérer les UEs par semestre
    @GET
    @Path("/semestre/{semestre}")
    public Response getUEsBySemestre(@PathParam("semestre") int semestre) {
        try {
            List<UniteEnseignement> result = new ArrayList<>();
            for (UniteEnseignement ue : unitesEnseignement) {
                if (ue.getSemestre() == semestre) {
                    result.add(ue);
                }
            }
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // POST - Ajouter une nouvelle UE
    @POST
    @Path("/addUE")
    public Response addUE(UniteEnseignement ue) {
        try {
            // Vérifier si l'UE existe déjà
            if (getUEByCodeFromList(ue.getCode()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Une UE avec ce code existe déjà")
                        .build();
            }

            // Ajouter l'UE à la liste
            unitesEnseignement.add(ue);

            return Response.status(Response.Status.CREATED)
                    .entity(ue)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // PUT - Mettre à jour une UE
    @PUT
    @Path("/modifierUE/{code}")
    public Response updateUE(@PathParam("code") int code, UniteEnseignement updatedUE) {
        try {
            for (int i = 0; i < unitesEnseignement.size(); i++) {
                if (unitesEnseignement.get(i).getCode() == code) {
                    unitesEnseignement.set(i, updatedUE);
                    return Response.ok(updatedUE).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("UE non trouvée")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // DELETE - Supprimer une UE
    @DELETE
    @Path("/deleteUE/{code}")
    public Response deleteUE(@PathParam("code") int code) {
        try {
            Iterator<UniteEnseignement> iterator = unitesEnseignement.iterator();
            while (iterator.hasNext()) {
                UniteEnseignement ue = iterator.next();
                if (ue.getCode() == code) {
                    iterator.remove();
                    return Response.ok("UE supprimée avec succès").build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("UE non trouvée")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur: " + e.getMessage())
                    .build();
        }
    }

    // Méthode utilitaire pour rechercher une UE par code
    private UniteEnseignement getUEByCodeFromList(int code) {
        for (UniteEnseignement ue : unitesEnseignement) {
            if (ue.getCode() == code) {
                return ue;
            }
        }
        return null;
    }
}