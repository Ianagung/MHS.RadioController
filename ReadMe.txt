===========================================================================================================
Update aplikasi Core Datalink (tanggal 19 Agustus 2016)
Source 27-06-2016
Update:
	 Penambahan package org.len.tdl.general_var berisi Class model_variabel
		  tmp_trak_data (pindah dari Datalink | get set)
		  status_mc (get set)
		  item_request (pindah dari DataLink | get set)
		  ownnpu (pindah dari DataLink | get set)
	 MainTest
		  perubahan inisialisasi MC_RX dengan parameter mod_var
		  perubahan inisialisasi dtl dengan parameter mod_var
		  penambahan if untuk pemanggilan GUI_Show_Trak_Tx pada timer
		  limitasi pemanggilan GUI_Show_trak dengan menggunakan status_mc
		  dtl.trak_data diganti dengan mod_var tmp_trak_data (get set)
		  dtl.dtdma_ownnpu diganti dengan mod_var ownnpu (get set)
	 Datalink114
		  tambah constructor dengan parameter model_variabel
		  trak_data diganti dengan mod_var tmp_trak_data (get set)
		  dtdma_ownnpu diganti dengan mod_var ownnpu (get set)
		  item_request diganti dengan mod_var item_request (get set)
	 multicast_rx
		  tambah constructor dengan parameter model_variabel, constructor lama dikomen
		  dtl.trak_data diganti dengan mod_var item_request (get set)
		  dtl.item_request diganti dengan mod_var item_request (get set)
		  tambah -> --- mod_var.setStatus_mc(true) --- di run()
	 Penambahan DataLink_Constanta1, 

===========================================================================================================

Update aplikasi Core Datalinkn (22 Agustus 2016)
Update:
	MainTest
		dtl.trak_data_rx diganti dengan mod_var tmp_trak_data_rx (get set)
		penambahan fitur multicast
		penambahan MC_RX.start(), dtl.timer_send_trak_rx.start(), dtl.MCTX_SetSocket() pada button run
	Datalink114
		penambahan var int send_trak_length
		penambahan var bool multicast_send_trak
		penambahan var multicast_tx MC_TX (get set)
		dtl.trak_data_rx diganti dengan mod_var tmp_trak_data_rx (get set)
		penambahan Timer timer_send_trak_rx
		penambahan fungsi MCTX_SetSocket
	model_variable
		penambahan var byte[][] tmp_trak_data_rx (get set)
		penambahan var byte[] tmp_send_trak (get set)
		penambahan var DataLink_Constanta1 DEF
		inisialisasi var tmp_trak_data (baru)
	Perubahan Max Heap Size -> 1GB
	JDK 1.8


===========================================================================================================
Update aplikasi Core Datalinkn (23 Agustus 2016)		
Update:
	MainTest
		penambahan var multicast_tx MC_TX 
		dtl.MCTX_SetSocket() diganti dengan MC_TX.set_socket()
		dtl.timer_send_trak_rx diganti dengan MC_TX().start()
		penambahan proses suspend dan resume pada checkbox transmit data
	Datalink114
		var multicasat_tx MC_TX dihapus beserta getter dan setternya
	multicast_tx
		perubahan class dari extends thread -> implements Runnable
		penambahan var model_variabel mod_var
		penambahan var Datalink_Constanta1 DEF
		penambahan var int send_trak_length
		penambahan var Thread t
		penambahan var String ThreadName
		penambahan var bool suspended 
		perubahan pada constructor dengan penambahan parameter model_variabel 
		pemindahan isi dari timer_send_trak_rx (Datalink114) ke method run() dan isi dari run() di comment 
		penambahan method start(), stop(), suspend(), resume()
==================================================================================================================
Update aplikasi Core Datalink (29 Agustus 2016)
Source: 23 Agustus 2016
Update:
	MainTest
		Penambahan checkbox mute
		Penambahan penambahan var sync_view sync & view
		dtl.dtdma diganti dengan mod_var dtdma_npu_txtime (get set)
		penambahan button minimode
	model_variabel
		penambahan var boolean mute (get set), data berasal dari checkbox di main test
		penambahan var int dtdma_npu_txtime (get set)
	Penambahan Class sync_view
		penambahan NPU 21 - 30
		GUI_Show_NPU_Active() penambahan proses NPU 21-30
		GUI_Show_NPU_Tick() penambahan proses NPU 21-30
	Penambahan Class Say_number	
		Penambahan constructor baru dengan parameter model_variabel
		Penambahan var mod_var
		Pembatasan pengerjaan proses init_say() dengan var !mod_var.isMute()
		penambahan var AudioInputStream[] soundArray
		penambahan var AudioInputStream soundIStream (pengganti soundFile)
		penambahan statement for pada constructor untuk mengisi soundArray dengan semua file .wav
		soundFile tidak digunakan lagi
		pada method start_say sound diisi dengan soundIStream (sebelumnya diisi soundFile, block trycatch hilang)
==================================================================================================================
Update aplikasi Core Datalink (31 Agustus 2016 v1)
Source: 23 Agustus 2016
Update:
	Ganti panjang data menjadi 32 Bytes
==================================================================================================================
Update aplikasi Core Datalink (31 Agustus 2016 v2)
Source: 31 Agustus 2016 v1 digabungkan dengan project tanggal 29 Agustus 2016
Update: 
	Maintest
	model_variabel
	sync_view
	Say_number
==================================================================================================================
Update aplikasi Core Datalink (31 Agustus 2016 v3)
Source: 31 Agustus v2 (digabungkan dengan project tanggal 24 Agustus 2016
Update:
	minimode open -> maintest dispose
	xml_CRUD -> Baru
	file.xml -> attached to project
==================================================================================================================
Update aplikasi Core Datalink (02 September 2016)
Update:
	Kirim message ke trak displayer

==================================================================================================================
Update aplikasi Core Datalink (07 September 2016)
Update:
	Datalink_Constanta
		Penamahan jumlah data yang ditampung pada array
			Speed -> 3 Bytes
			Course -> 3 Bytes
		Pergeseran index akibat perubahan diatas
		penggantian index yang ditulis manual menjadi mengacu pada DatalinkConstanta (sebagian)
			-Datalink114, 
				sendmsg()
				read_trak_tx()
				read_trak_rx()
				
			-MainTest
				GUI_Show_Trak_Rcv()
==================================================================================================================
Update aplikasi Core Datalink (14 September 2016)
Update:
	Datalink114
		get_circle_rx()
			longitude -> 4 Byte
			latitude -> 4 Byte
			range -> 4 Byte
		get_polyline_rx
			longitude -> 4 Byte
			latitude -> 4 Byte
	multicast_tx
		MulticastSocket -> DatagramSocket
		outPacket pindah ke inisialisasi, outPacket tidak di 'new' setiap kirim_multicast() tapi di setData(), setLength dll.
==================================================================================================================
Update aplikasi CoreDatalink (16 September 2016) - bisa meneruskan message dari trak generator + menerima message 
Update:
	multicast_rx
		penambahan parameter Datalink114 (dtl) pada constructor 
		run()
			panjang message tidak dikurangi DEF.LENGTH_TOPIC
	xml_CRUD
		file.xml menggunakan file di Documents\tools_datalink\xml\file.xml
	Say_Number
		menggunakan folder Documents\tools_datalink\Say_NPU\
==================================================================================================================
			
		
	
		


















	
