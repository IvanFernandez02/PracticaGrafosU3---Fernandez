import requests
from flask import Blueprint, render_template, jsonify
import requests

router = Blueprint('router', __name__)

@router.route('/')
def home():
    return render_template('template.html')


@router.route('/mapagrafos')
def mapagrafos():
    r = requests.get("http://localhost:8099/myapp/puntoencuentro/mapadegrafos")
    
    try:
       if r.status_code == 200:
            graph_data = r.json()
            return render_template('grafos.html', graph_data=graph_data)
       else:
            print("Error al obtener grafo: ", r.text)
            return jsonify({"error": "No se pudo obtener el grafo"}), r.status_code

    except requests.exceptions.RequestException as e:
        print("Error de conexión: ", str(e))
        return jsonify({"error": "No se pudo conectar con el servidor"}), 500

@router.route('/adyacencias')
def adyacenciasaleatorias():
    r = requests.get("http://localhost:8099/myapp/puntoencuentro/misgrafos")
    print(r.status_code)  
    print(r.text)         

    data = r.json()
    print(data)
    return render_template('adyacencias.html', lista=data)

@router.route('/nueva_adyacencia', methods=['GET'])
def nueva_adyacencia():
    r = requests.get("http://localhost:8099/myapp/puntoencuentro/adyacencias_aleatorias")

    try:
        if r.status_code == 200:
            data = r.json()
            return jsonify(data)  
        else:
            return jsonify({"error": "No se pudo obtener la adyacencia"}), r.status_code

    except requests.exceptions.RequestException as e:
        print("Error de conexión: ", str(e))
        return jsonify({"error": "No se pudo conectar con el servidor"}), 500
    
@router.route('/calculo_camino_corto/<int:inicio>/<int:fin>/<int:algoritmo>', methods=['GET'])
def calcular_camino_corto(inicio, fin, algoritmo):
    try:
        url = f"http://localhost:8099/myapp/puntoencuentro/camino_corto/{inicio}/{fin}/{algoritmo}"
        r = requests.get(url)

        if r.status_code == 200:
            data = r.json()
            return jsonify(data)
        else:
            return jsonify({"error": "No se pudo calcular el camino corto"}), r.status_code

    except requests.exceptions.RequestException as e:
        print("Error de conexión:", str(e))
        return jsonify({"error": "No se pudo conectar con el servidor de cálculo"}), 500
    
@router.route('/camino', methods=['GET'])
def mostrar_formulario():
    r = requests.get("http://localhost:8099/myapp/puntoencuentro/misgrafos")
    data = r.json()
    return render_template('camino.html', data= data)  # Nombre del archivo de tu template

@router.route('/busquedaanchura/<int:inicio>', methods=['GET'])
def ejecutar_busquedaanchura(inicio):
    try:
        url = f"http://localhost:8099/myapp/puntoencuentro/busquedaanchura/{inicio}"
        r = requests.get(url)

        if r.status_code == 200:
            if r.text.strip():
                data = r.json()
                return jsonify(data)  
            else:
                return jsonify({"error": "La respuesta está vacía"}), 500
        else:
            return jsonify({"error": f"Error al ejecutar BUSQUEDAANCHURA. Código de estado: {r.status_code}"}), r.status_code

    except requests.exceptions.RequestException as e:
        print("Error de conexión:", str(e))
        return jsonify({"error": "No se pudo conectar con el servidor de busquede an anchura"}), 500



