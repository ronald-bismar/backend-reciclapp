package com.example.reciclapp.data.repositories

import android.util.Log
import com.example.reciclapp.domain.entities.Comentario
import com.example.reciclapp.domain.entities.Material
import com.example.reciclapp.domain.entities.Producto
import com.example.reciclapp.domain.entities.UbicacionGPS
import com.example.reciclapp.domain.entities.Usuario
import com.example.reciclapp.domain.repositories.VendedorRepository
import com.example.reciclapp.domain.usecases.comentario.CrearComentarioUseCase
import com.example.reciclapp.domain.usecases.producto.RegistrarProductoUseCase
import com.example.reciclapp.model.util.GenerateID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VendedorRepositoryImpl @Inject constructor(
    private val service: FirebaseFirestore,
    private val registrarProductoUseCase: RegistrarProductoUseCase,
    private val crearComentarioUseCase: CrearComentarioUseCase
) : VendedorRepository {

    override suspend fun getVendedor(idVendedor: Int): Usuario? {
        val snapshot = service.collection("usuario")
            .document(idVendedor.toString())
            .get()
            .await()
        return snapshot.toObject(Usuario::class.java)
    }

    override suspend fun getVendedores(): MutableList<Usuario> {
        val vendedores = mutableListOf<Usuario>()
        val querySnapshot = service.collection("usuario")
            .whereEqualTo("tipoDeUsuario", "vendedor")
            .get()
            .await()
        for (document in querySnapshot.documents) {
            val usuario = document.toObject(Usuario::class.java)
            usuario?.let { vendedores.add(it) }

            if (usuario != null) {
                Log.d("Usuarios","user: ${usuario.nombre}")
            }
        }
        return vendedores
    }

    override suspend fun publicarProducto(producto: Producto, user: Usuario) {
        try {
            registrarProductoUseCase.execute(producto)
        } catch (e: Exception) {
            Log.d("Exception", "No se pudo publicar el producto $e")
        }
    }

    override suspend fun verMapaConCompradoresCercanos(ubicacionGPS: UbicacionGPS) {
        TODO("Not yet implemented")
    }

    override fun llamarAComprador(comprador: Usuario) {
        TODO("Not yet implemented")
    }

    override fun enviarMensajeAComprador(comprador: Usuario) {
        TODO("Not yet implemented")
    }

    override suspend fun compararPrecioEntreCompradores(compradores: List<Usuario>): List<Usuario> {
        // Se lista a los compradores que dan un precio más alto por el material

        // Precio en general (la suma de todos sus materiales que compra)
        val materiales = mutableListOf<Material>()
        val querySnapshot = service.collection("material")
            .get()
            .await()
        for (document in querySnapshot.documents) {
            val material = document.toObject(Material::class.java)
            material?.let { materiales.add(it) }
        }

        // Crear un mapa para almacenar la suma de los precios por idComprador
        val sumasPorComprador = mutableMapOf<Int, Double>()

        // Calcular la suma de los precios para cada idComprador
        for (material in materiales) {
            val idComprador = material.idComprador
            val precioMaterial = material.precio

            sumasPorComprador[idComprador] = sumasPorComprador.getOrDefault(idComprador, 0.0) + precioMaterial
        }

        // Ordenar los compradores según la suma de los precios de sus materiales en orden descendente
        val compradoresOrdenados = compradores.sortedByDescending { comprador ->
            sumasPorComprador.getOrDefault(comprador.idUsuario, 0.0)
        }

        return compradoresOrdenados
    }

    override suspend fun comentarAComprador(comentario: Comentario) {
        comentario.idComentario = GenerateID()
        crearComentarioUseCase.execute(comentario)

    }

    override fun verListaDeCompradoresCercanos(compradores: List<Usuario>) {
        TODO("Not yet implemented")
    }
}