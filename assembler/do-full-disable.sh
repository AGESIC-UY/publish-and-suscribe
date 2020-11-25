# Controlamos que estemos corriendo con root
if [ "$(whoami)" != "root" ]; then
  echo "Por favor ejecute con SUDO/ROOT"
  exit
fi

# Vamos a la carpeta de la PDI
cd /opt/pdi

# Deshabilitamos y eliminamos todos los servicios de systemd
for SRV in `ls *.service`; do
    systemctl disable $SRV
    rm /etc/systemd/system/$SRV
done
