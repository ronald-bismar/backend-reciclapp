package com.example.reciclapp.domain.usecases.material

import com.example.reciclapp.domain.entities.Material
import com.example.reciclapp.domain.repositories.MaterialRepository
import javax.inject.Inject

class ListarMaterialesPorCompradorUseCase @Inject constructor(private val materialRepository: MaterialRepository) {
    suspend fun execute(idUsuario: Int): MutableList<Material> {
        return materialRepository.listarMaterialesPorComprador(idUsuario)
    }
}