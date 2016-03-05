productor_id = location.pathname.split('/')[2]

$ ->
  $.get "/productores/" + productor_id, (productores) ->
    $.each productores, (index, productor) ->
      $("#name").html productor.nombre
      $("#carnet").html productor.carnet
      $("#telefono").html productor.telefono
      $("#direccion").html productor.direccion
      $("#cuenta").html productor.cuenta
      $("#asociacion").html productor.asociacion
